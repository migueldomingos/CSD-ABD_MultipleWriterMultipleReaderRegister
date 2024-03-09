package replica;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

import java.net.URI;

import static replica.RegisterSingleton.RegisterType.*;

public class ReplicaMain {

    public static final String BASE_URI = "http://localhost:%d/";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ReplicaMain <port> <registerType>");
            System.out.println("port: The port to listen on.");
            System.out.printf("registerType: %d -> Correct, %d -> Crash, %d -> Byzantine.",
                    CORRECT.ordinal(), CRASH.ordinal(), BYZANTINE.ordinal());
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        RegisterSingleton.RegisterType type = RegisterSingleton.RegisterType.values()[Integer.parseInt(args[1])];
        RegisterSingleton.setRegister(type);
        String uri = String.format(BASE_URI, port);
        ResourceConfig config = new ResourceConfig(Endpoint.class);
        JdkHttpServerFactory.createHttpServer(URI.create(uri), config);
    }

}
