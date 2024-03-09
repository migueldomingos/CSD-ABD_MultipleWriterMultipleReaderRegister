package client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class Client {

    private static final Logger logger = LogManager.getLogger(Client.class);

    private final WebTarget target;
    
    private int timestamp = 0;

    public Client(String targetUrl) {
        this.target = ClientBuilder.newClient().target(targetUrl);
    }

    public float read() {
        logger.trace("Issuing read request");
        return target.path("read").request().get(RegisterContentPojo.class).getValue();
    }

    public void write(float value) {
        logger.trace("Issuing write request with value: {}", value);
        RegisterContentPojo registerContent = new RegisterContentPojo(timestamp, value);
        target.path("write").request().post(Entity.entity(registerContent, APPLICATION_JSON_TYPE));
        timestamp++;
        logger.debug("Incremented timestamp to: {}", timestamp);
    }
}
