package client;

public class ClientMain {

    public static void main(String[] args) {
        Client client = new Client("http://localhost:8080");
        for (int i = 0; i < 10; i++) {
            client.write(i);
            System.out.println(client.read());
        }
    }

}
