package client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class Client {

    private final WebTarget target;
    
    private int timestamp = 0;

    public Client(String targetUrl) {
        this.target = ClientBuilder.newClient().target(targetUrl);
    }

    public float read() {
        return target.path("read").request().get(RegisterContentPojo.class).getValue();
    }

    public void write(float value) {
        RegisterContentPojo registerContent = new RegisterContentPojo(timestamp, value);
        target.path("write").request().post(Entity.entity(registerContent, APPLICATION_JSON_TYPE));
        timestamp++;
    }
}
