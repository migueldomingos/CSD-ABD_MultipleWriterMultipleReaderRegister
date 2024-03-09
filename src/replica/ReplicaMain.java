package replica;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

import java.net.URI;

public class ReplicaMain {

    public static final String BASE_URI = "http://localhost:%d/";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ReplicaMain <port>");
            System.out.println("port: The port to listen on.");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        String uri = String.format(BASE_URI, port);
        ResourceConfig config = new ResourceConfig(Endpoint.class);
        JdkHttpServerFactory.createHttpServer(URI.create(uri), config);
    }

}
