import java.util.HashMap;
import java.util.Map;

public class Main {

    private static TravelAgency server;
    private static Thread serverThread;

    public static void main(String[] args) {
        startServer();
        createClient();
        createClient();

    }

    public static void createClient() {
        int hotelID=0, airlineID=0;

        Client client = new Client();
        client.startConnection("127.0.0.1", 6666);
        client.setFirstLoginFlag(true);

        // Get all hotels and airlines
        System.out.println("Get All Hotels and Airlines");
        String response=client.sendMessage(new TripDetail(0, 0, 0));
        System.out.println("Server: \n" + response);

        client.stopConnection();

        String hotelsPart = response.substring(response.indexOf("Hotels: ")+9, response.indexOf("}"));
        String airlinesPart = response.substring(response.indexOf("Airlines: ")+11, response.indexOf("Status-code")-3);
        HashMap<Integer, String> hotelsMap = new HashMap<Integer, String>();
        HashMap<Integer, String> airlinesMap = new HashMap<Integer, String>();

        hotelsMap.put(1, hotelsPart.substring(2, hotelsPart.indexOf(",")));
        hotelsMap.put(2, hotelsPart.substring(hotelsPart.indexOf("2")+2, hotelsPart.length()));

        airlinesMap.put(1, airlinesPart.substring(2, airlinesPart.indexOf(",")));
        airlinesMap.put(2, airlinesPart.substring(airlinesPart.indexOf("2")+2, airlinesPart.length()));

        // TODO: müşteri istediği otel havayolu şirketi vs seçecek gui üzerinden
        // todo: guiden gelecek bu cevaplar
        String hotel_answer="otel erzincan";
        String airline_answer="erzincan turizm";

        // Find id of the airline
        for (Map.Entry<Integer, String> entry:airlinesMap.entrySet()) {
            if (entry.getValue().equals(airline_answer)) {
                airlineID=entry.getKey();
            }
        }
        // Find id of the hotel
        for (Map.Entry<Integer, String> entry:hotelsMap.entrySet()) {
            if (entry.getValue().equals(hotel_answer)) {
                hotelID=entry.getKey();
            }
        }

        TripDetail tripDetail = new TripDetail(1, airlineID, hotelID);
        clientRequest(client, tripDetail);

        //server.stop();
    }

    public static void clientRequest(Client client, TripDetail tripDetail) {
        client.setFirstLoginFlag(false);
        client.startConnection("127.0.0.1", 6666);
        System.out.println("Client: " + tripDetail.numberOfTravellers + " " + tripDetail.preferredAirline + " " + tripDetail.preferredHotel);
        String response2=client.sendMessage(tripDetail);
        System.out.println("Server: \n" + response2);
    }

    public static void startServer() {
        server = new TravelAgency();
        serverThread = new Thread(server);
        serverThread.start();
    }
}
