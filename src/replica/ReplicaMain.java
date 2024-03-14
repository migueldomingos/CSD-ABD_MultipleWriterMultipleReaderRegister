package replica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

import java.net.URI;

import static replica.RegisterSingleton.RegisterType.*;

public class ReplicaMain {

    static {
        System.setProperty("log4j.configurationFile", "config/log4j2.xml");
    }

    public static final String BASE_URI = "http://localhost:%d/";

    private static final Logger logger = LogManager.getLogger(ReplicaMain.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ReplicaMain <port> <registerType>");
            System.out.println("port: The port to listen on.");
            System.out.printf("registerType: %d -> Correct, %d -> Crash, %d -> Byzantine.\n",
                    CORRECT.ordinal(), CRASH.ordinal(), BYZANTINE.ordinal());
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        String uri = String.format(BASE_URI, port);
        logger.info("Initializing server on URL: {}", uri);
        RegisterSingleton.RegisterType type = RegisterSingleton.RegisterType.values()[Integer.parseInt(args[1])];
        RegisterSingleton.setRegister(type);
        logger.info("Instantiating register with type: {}", type.name());
        ResourceConfig config = new ResourceConfig(Endpoint.class);
        JdkHttpServerFactory.createHttpServer(URI.create(uri), config);
    }

}
