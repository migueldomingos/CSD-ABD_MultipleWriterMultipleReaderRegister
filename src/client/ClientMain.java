package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ClientMain {

    private static final Logger logger = LogManager.getLogger(ClientMain.class);

    public static void main(String[] args) throws IOException {
        ClientSingleWriter client = new ClientSingleWriter();
        for (int i = 0; i < 10; i++) {
            write(i, client);
            read(i, client);
        }
    }

    private static void write(int value, ClientSingleWriter client) {
        logger.info("Issuing write request with value: {}", value);
        long start = System.currentTimeMillis();
        client.write(value);
        long end = System.currentTimeMillis();
        logger.debug("Write request took: {}ms", end - start);
    }

    private static void read(int i, ClientSingleWriter client) {
        logger.info("Issuing read request on iteration: {}", i);
        long start = System.currentTimeMillis();
        float value = client.read();
        long end = System.currentTimeMillis();
        logger.info("Read value: {}", value);
        logger.debug("Read request took: {}ms", end - start);
    }

}
