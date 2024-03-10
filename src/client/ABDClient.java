package client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojos.RegisterContentPojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;


public class ABDClient {

    private static final Logger logger = LogManager.getLogger(ABDClient.class);

    /*
    * This list contains the web targets of the replicas.
    * The web targets are used to issue requests to the replicas.
    */
    private final List<WebTarget> targets;

    private final int readQuorum;

    private final int writeQuorum;

    /*
    * This integer represents the writer ID.
    * The writer ID is used to identify the client that wrote the content.
    * The writer ID is used to break ties when multiple contents have the same timestamp.
    */
    private final int writerId;

    /*
    * This constructor initializes the ABD client.
    * The constructor reads the configuration file and initializes the web targets.
     */
    public ABDClient(int writerId) throws IOException {
        this.writerId = writerId;
        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get("config/config.properties")));
        this.readQuorum = Integer.parseInt(props.getProperty("read_quorum", "5"));
        this.writeQuorum = Integer.parseInt(props.getProperty("write_quorum", "5"));
        int numReplicas = Integer.parseInt(props.getProperty("num_replicas", "10"));
        int startPort = Integer.parseInt(props.getProperty("start_port", "8080"));
        targets = new ArrayList<>(numReplicas);
        for (int i = 0; i < numReplicas; i++) {
            targets.add(ClientBuilder.newClient().target("http://localhost:" + (startPort + i)));
        }
    }


    /*
    * This method is used to read from the distributed register.
    * The method issues a read request to a quorum of replicas.
    * The method then processes the responses and helps the replicas by broadcasting the highest timestamp response.
    */
    public float read() {
        logger.info("Issuing read request");
        logger.trace("Reading from quorum");
        List<RegisterContentPojo> quorumResponses = getReadQuorum();
        logger.trace("Processing read quorum");
        RegisterContentPojo result = getMostUpdatedContent(quorumResponses);
        logger.debug("Highest timestamp: {}, lowest writer ID: {}, value: {}",
                result.getTimestamp(), result.getId(), result.getValue());
        broadcastWrite(result);
        return result.getValue();
    }

    /*
    * This method is used to get the most updated content from a quorum of responses.
    * The most updated content is the one with the highest timestamp.
    * If there are multiple contents with the highest timestamp, the one with the lowest writer ID is chosen.
    * If the quorum sizes are well-chosen, the no inconsistencies should arise between client reads.
    */
    private RegisterContentPojo getMostUpdatedContent(Collection<RegisterContentPojo> quorumResponses) {
        int highestTimestamp = quorumResponses.stream()
                .mapToInt(RegisterContentPojo::getTimestamp)
                .max()
                .orElseThrow(() -> new IllegalStateException("No timestamps received"));
        List<RegisterContentPojo> highestTimestampResponses = quorumResponses.stream()
                .filter(response -> response.getTimestamp() == highestTimestamp)
                .collect(Collectors.toList());
        int lowestWriterId = highestTimestampResponses.stream()
                .mapToInt(RegisterContentPojo::getId)
                .min()
                .orElseThrow(() -> new IllegalStateException("No writer IDs received"));
        return highestTimestampResponses.stream()
                .filter(response -> response.getId() == lowestWriterId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No result found"));
    }

    /*
    * This method is used to write to the distributed register.
    * The method issues a write request to a quorum of replicas.
    * Initially, the method reads the most updated timestamp from the replicas.
    * It then writes a new value with a timestamp higher than the most updated timestamp read.
     */
    public void write(float value) {
        logger.info("Issuing write request");
        logger.trace("Reading most updated timestamp");
        int timestamp = readMostUpdatedTimestamp();
        logger.trace("Most updated timestamp: {}", timestamp);
        RegisterContentPojo content = new RegisterContentPojo(timestamp + 1, writerId, value);
        broadcastWrite(content);
    }

    @SneakyThrows
    private int readMostUpdatedTimestamp() {
        List<RegisterContentPojo> quorumResponses = getReadQuorum();
        return quorumResponses.stream()
                .mapToInt(RegisterContentPojo::getTimestamp)
                .max()
                .orElseThrow(() -> new IllegalStateException("No timestamps received"));
    }

    @SneakyThrows
    private List<RegisterContentPojo> getReadQuorum() {
        logger.trace("Issuing read request");
        BlockingQueue<RegisterContentPojo> responses = new LinkedBlockingQueue<>();
        List<RegisterContentPojo> quorumResponses = new ArrayList<>(readQuorum);
        for (WebTarget target : targets)
            new Thread(() -> readFromReplica(target, responses)).start();
        while (quorumResponses.size() < readQuorum)
            quorumResponses.add(responses.take());
        return quorumResponses;
    }

    /*
     * This method is used to read from a replica.
     * The response is stored in the responses queue.
     * Because the responses queue is a blocking queue, the main thread will wait until a response is received.
     */
    private static void readFromReplica(WebTarget target, BlockingQueue<RegisterContentPojo> responses) {
        RegisterContentPojo registerContent = target.path("read").request().get(RegisterContentPojo.class);
        logger.debug("Received read response from {} with timestamp: {}, value: {}",
                target.getUri(), registerContent.getTimestamp(), registerContent.getValue());
        responses.add(registerContent);
    }

    @SneakyThrows
    private void broadcastWrite(RegisterContentPojo content) {
        logger.trace("Issuing write request with value: {}", content.getValue());
        BlockingQueue<Boolean> responses = new LinkedBlockingQueue<>();
        int numResponses = 0;
        for (WebTarget target : targets)
            new Thread(() -> writeToReplica(target, content, responses)).start();
        while (numResponses < writeQuorum) {
            responses.take();
            numResponses++;
        }
    }

    /*
     * This method is used to write to a replica.
     * The response is stored in the responses queue.
     * Because the responses queue is a blocking queue, the main thread will wait until a response is received.
     */
    private void writeToReplica(WebTarget target, RegisterContentPojo registerContent, BlockingQueue<Boolean> responses) {
        target.path("write").request().post(Entity.entity(registerContent, APPLICATION_JSON_TYPE));
        responses.add(true);
    }

}
