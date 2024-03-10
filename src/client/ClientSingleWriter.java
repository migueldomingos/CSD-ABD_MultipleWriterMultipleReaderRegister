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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class ClientSingleWriter {

    private static final Logger logger = LogManager.getLogger(ClientSingleWriter.class);

    private final List<WebTarget> targets;
    
    private int timestamp = 0;

    private final int readQuorum;

    private final int writeQuorum;

    public ClientSingleWriter() throws IOException {
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

    @SneakyThrows
    public float read() {
        logger.trace("Issuing read request");
        BlockingQueue<RegisterContentPojo> responses = new LinkedBlockingQueue<>();
        List<RegisterContentPojo> quorumResponses = new ArrayList<>(readQuorum);
        for (WebTarget target : targets)
            new Thread(() -> readFromReplica(target, responses)).start();
        while (quorumResponses.size() < readQuorum)
            quorumResponses.add(responses.take());
        return processReadQuorum(quorumResponses);
    }

    private static void readFromReplica(WebTarget target, BlockingQueue<RegisterContentPojo> responses) {
        RegisterContentPojo registerContent = target.path("read").request().get(RegisterContentPojo.class);
        logger.debug("Received read response from {} with timestamp: {}, value: {}",
                target.getUri(), registerContent.getTimestamp(), registerContent.getValue());
        responses.add(registerContent);
    }

    private static Float processReadQuorum(List<RegisterContentPojo> quorumResponses) {
        int highestTimestamp = quorumResponses.stream()
                .mapToInt(RegisterContentPojo::getTimestamp)
                .max()
                .orElseThrow(() -> new IllegalStateException("No responses received"));
        List<RegisterContentPojo> highTimestampResponses = quorumResponses.stream()
                .filter(r -> r.getTimestamp() == highestTimestamp)
                .collect(Collectors.toList());
        int lowestId = highTimestampResponses.stream()
                .mapToInt(RegisterContentPojo::getId)
                .min()
                .orElseThrow(() -> new IllegalStateException("No responses received"));
        return highTimestampResponses.stream()
                .filter(r -> r.getId() == lowestId)
                .map(RegisterContentPojo::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No responses received"));
    }

    @SneakyThrows
    public void write(float value) {
        logger.trace("Issuing write request with value: {}", value);
        BlockingQueue<Boolean> responses = new LinkedBlockingQueue<>();
        int numResponses = 0;
        RegisterContentPojo registerContent = new RegisterContentPojo(timestamp, 0, value);
        for (WebTarget target : targets)
            new Thread(() -> writeToReplica(target, registerContent, responses)).start();
        while (numResponses < writeQuorum) {
            responses.take();
            numResponses++;
        }
        timestamp++;
        logger.debug("Incremented timestamp to: {}", timestamp);
    }

    private void writeToReplica(WebTarget target, RegisterContentPojo registerContent, BlockingQueue<Boolean> responses) {
        target.path("write").request().post(Entity.entity(registerContent, APPLICATION_JSON_TYPE));
        responses.add(true);
    }
}
