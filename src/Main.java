public class Main {

    private static TravelAgency server;
    private static Thread serverThread;

    public static void main(String[] args) {
        startServer();
        createClient();
        createClient();

    }

    public static void createClient() {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);
        client.setFirstLoginFlag(true);

        // Get all hotels and airlines
        TripDetail tripDetail = new TripDetail(0, "-", "-");
        System.out.println("Get All Hotels and Airlines");
        String response=client.sendMessage(tripDetail);
        System.out.println("\nServer: " + response);

        client.stopConnection();
        client.startConnection("127.0.0.1", 6666);

        clientRequest(client);

        //server.stop();
    }

    public static void clientRequest(Client client) {
        client.setFirstLoginFlag(false);
        // TODO: müşteri istediği otel havayolu şirketi vs travel agency ye bildirir.
        int numberOfTravellers=1;
        String preferredAirline="airline-1";
        String preferredHotel="hotel-1";

        TripDetail tripDetail = new TripDetail(numberOfTravellers, preferredAirline, preferredHotel);
        System.out.println("Client: " + tripDetail.numberOfTravellers + " " + tripDetail.preferredAirline + " " + tripDetail.preferredHotel);
        String response2=client.sendMessage(tripDetail);
        System.out.println("\nServer: " + response2);
    }

    public static void startServer() {
        server = new TravelAgency();
        serverThread = new Thread(server);
        serverThread.start();
    }
}
