package replica;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("")
public class Endpoint {

    private final Register register = RegisterSingleton.getRegister();

    public Endpoint() throws Exception {}

    @GET
    @Path("read")
    @Produces(APPLICATION_JSON)
    public Response read() {
        int timestamp = register.getTimestamp();
        float value = register.getValue();
        System.out.println("Reading timestamp: " + timestamp + " value: " + value);
        LatencySimulator.simulateLatency();
        return Response.ok(new RegisterContentPojo(timestamp, value)).build();
    }

    @POST
    @Path("write")
    @Consumes(APPLICATION_JSON)
    public Response write(RegisterContentPojo registerContent) {
        register.setTimestamp(registerContent.getTimestamp());
        register.setValue(registerContent.getValue());
        LatencySimulator.simulateLatency();
        return Response.ok().build();
    }

}
