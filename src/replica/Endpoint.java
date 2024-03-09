package replica;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("")
public class Endpoint {

    private static final Logger logger = LogManager.getLogger(Endpoint.class);

    private final Register register = RegisterSingleton.getRegister();

    public Endpoint() throws Exception {
        logger.info("Initiated endpoint");
    }

    @GET
    @Path("read")
    @Produces(APPLICATION_JSON)
    public Response read() {
        int waitTimeReceive = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of receiving request: {}ms", waitTimeReceive);
        int timestamp = register.getTimestamp();
        float value = register.getValue();
        logger.debug("Reading timestamp: {}, value: {}", timestamp, value);
        int waitTimeSend = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
        return Response.ok(new RegisterContentPojo(timestamp, value)).build();
    }

    @POST
    @Path("write")
    @Consumes(APPLICATION_JSON)
    public Response write(RegisterContentPojo registerContent) {
        register.updateRegister(registerContent.getTimestamp(), registerContent.getValue());
        int waitTimeReceive = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of receiving request: {}ms", waitTimeReceive);
        logger.debug("Writing timestamp: {}, value: {}",
                registerContent.getTimestamp(), registerContent.getValue());
        int waitTimeSend = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
        return Response.ok().build();
    }

}
