package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ClientMain {

    private static final Logger logger = LogManager.getLogger(ClientMain.class);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java ClientMain <processId>");
            System.out.println("processId: ID of the client (integer). Each client must have a distinct ID!");
            System.exit(-1);
        }
        int writerId = Integer.parseInt(args[0]);
        ABDClient client = new ABDClient(writerId);
        for (int i = 0; i < 10; i++) {
            write(i, client);
            read(i, client);
        }
    }

    private static void write(int value, ABDClient client) {
        logger.info("Issuing write request with value: {}", value);
        long start = System.currentTimeMillis();
        client.write(value);
        long end = System.currentTimeMillis();
        logger.debug("Write request took: {}ms", end - start);
    }

    private static void read(int i, ABDClient client) {
        logger.info("Issuing read request on iteration: {}", i);
        long start = System.currentTimeMillis();
        float value = client.read();
        long end = System.currentTimeMillis();
        logger.info("Read value: {}", value);
        logger.debug("Read request took: {}ms", end - start);
    }

}
