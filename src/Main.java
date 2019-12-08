public class Main {

    static Server server;

    public static void main(String[] args) {
        startServer();
        createClient();
    }

    public static void createClient() {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);

        // TODO: müşteri istediği otel havayolu şirketi vs travel agency ye bildirir.

        String msg1="FURKAN";
        System.out.println("Client: " + msg1);
        String response = client.sendMessage(msg1);
        System.out.println("Server: " + response);

        //server.stop();
    }

    public static void startServer() {
        server = new Server();
        Thread thread = new Thread(server);
        thread.start();
    }
}
