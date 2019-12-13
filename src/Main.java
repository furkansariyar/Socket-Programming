public class Main {

    static TravelAgency server;

    public static void main(String[] args) {
        startServer();
        createClient();
        createClient();

    }

    public static void createClient() {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);
        client.setFirstLoginFlag(true);
        //TODO: get all hotels and airplanes


        //client.setFirstLoginFlag(false);
        // TODO: müşteri istediği otel havayolu şirketi vs travel agency ye bildirir.
        int numberOfTravellers=1;
        String preferredAirline="airline-1";
        String preferredHotel="hotel-1";



        TripDetail tripDetail = new TripDetail(numberOfTravellers, preferredAirline, preferredHotel);
        System.out.println("Client: " + tripDetail.numberOfTravellers + " " + tripDetail.preferredAirline + " " + tripDetail.preferredHotel);
        String response=client.sendMessage(tripDetail);
        System.out.println("Server: " + response);


        //server.stop();


    }

    public static void startServer() {
        server = new TravelAgency();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
