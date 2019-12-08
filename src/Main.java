import java.util.ArrayList;

public class Main {

    static TravelAgency server;

    public static void main(String[] args) {
        startServer();
        createClient();
    }

    public static void createClient() {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);

        // TODO: müşteri istediği otel havayolu şirketi vs travel agency ye bildirir.
        int numberOfTravellers=1;
        ArrayList<String> preferredAirlines = new ArrayList<String>();
        ArrayList<String> preferredHotels = new ArrayList<String>();
        preferredAirlines.add("airline-1");
        preferredHotels.add("hotel-1");

        TripDetail tripDetail = new TripDetail(numberOfTravellers, preferredAirlines, preferredHotels);
        System.out.println("Client: " + tripDetail.numberOfTravellers + " " + tripDetail.preferredAirlines + " " + tripDetail.preferredHotels);
        String response=client.sendMessage(tripDetail);
        System.out.println("Server: " + response);

/*        System.out.println("Client: " + numberOfTravellers);
        String response = client.sendMessage(String.valueOf(numberOfTravellers));
        System.out.println("Server: " + response);

        System.out.println("Client: " + preferredAirlines);
        response = client.sendMessage(preferredAirlines.get(0));
        System.out.println("Server: " + response);

        System.out.println("Client: " + preferredHotels);
        response = client.sendMessage(preferredHotels.get(0));
        System.out.println("Server: " + response);*/

        //server.stop();
    }

    public static void startServer() {
        server = new TravelAgency();
        Thread thread = new Thread(server);
        thread.start();
    }
}
