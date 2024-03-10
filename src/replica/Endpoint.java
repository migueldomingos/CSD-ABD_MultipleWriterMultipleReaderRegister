package replica;

/*
* These imports handle the RESTful API.
 */
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

/*
* Logging library to log the replica's actions.
* The logger is configured in the log4j2.xml file.
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
* Pojo (Plain Old Java Object) to represent the content of the register.
* Is serialized and deserialized to/from JSON without our intervention.
 */
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 *
 * This class represents the endpoint of the replica.
 * The endpoint is used to issue requests to the replica.
 */
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
        RegisterContentPojo pojo = register.getRegisterContent();
        logger.debug("Reading timestamp: {}, value: {}", pojo.getTimestamp(), pojo.getValue());
        int waitTimeSend = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
        return Response.ok(pojo).build();
    }

    @POST
    @Path("write")
    @Consumes(APPLICATION_JSON)
    public Response write(RegisterContentPojo registerContent) {
        register.updateRegister(registerContent.getTimestamp(), registerContent.getId(), registerContent.getValue());
        int waitTimeReceive = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of receiving request: {}ms", waitTimeReceive);
        logger.debug("Writing timestamp: {}, value: {}",
                registerContent.getTimestamp(), registerContent.getValue());
        int waitTimeSend = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
        return Response.ok().build();
    }

}
