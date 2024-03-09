package replica;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;


import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("")
public class Endpoint {

    private final Register register = new CorrectRegister();

    @Path("")
    public Response ping() {
        System.out.println("Received ping");
        return Response.ok("pong").build();
    }

    @Path("read")
    @Produces(APPLICATION_JSON)
    public Response read() {
        int timestamp = register.getTimestamp();
        float value = register.getValue();
        System.out.println("Reading timestamp: " + timestamp + " value: " + value);
        return Response.ok(Pair.of(timestamp, value)).build();
    }

    /*@Path("write")
    @Consumes(APPLICATION_JSON)
    public Response write(Pair<Integer, Float> pair) {
        register.setTimestamp(pair.getLeft());
        register.setValue(pair.getRight());
        return Response.ok().build();
    }*/

}
