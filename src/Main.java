import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static TravelAgency server;
    private static Thread serverThread;
    private static String host="127.0.0.1";
    // TODO: ports can be defined here

    public static void main(String[] args) {
        startServer();
        createClient();
        //createClient();
    }

    public static void createClient() {
        int hotelID=0, airlineID=0;
        String dateStart, dateEnd;

        Client client = new Client();
        client.startConnection(host, 8070);
        client.setFirstLoginFlag(true);

        // Get all hotels and airlines
        //System.out.println("Get All Hotels and Airlines");
        String response=client.sendMessage(new TripDetail(0, 0, 0, "", ""), false);
        System.out.println("Server:\n" + response);

        client.stopConnection();

        String hotelsPart = response.substring(response.indexOf("Hotels: ")+9, response.indexOf("}"));
        String airlinesPart = response.substring(response.indexOf("Airlines: ")+11, response.length()-3);
        HashMap<Integer, String> hotelsMap = new HashMap<Integer, String>();
        HashMap<Integer, String> airlinesMap = new HashMap<Integer, String>();

        hotelsMap.put(1, hotelsPart.substring(2, hotelsPart.indexOf(",")));
        hotelsMap.put(2, hotelsPart.substring(hotelsPart.indexOf("2")+2, hotelsPart.length()));

        airlinesMap.put(1, airlinesPart.substring(2, airlinesPart.indexOf(",")));
        airlinesMap.put(2, airlinesPart.substring(airlinesPart.indexOf("2")+2, airlinesPart.length()));

        GUI gui = new GUI(hotelsMap, airlinesMap);
        hotelID=gui.getHotelID();
        airlineID=gui.getAirlineID();
        dateStart=gui.getDateStart();
        dateEnd=gui.getDateEnd();

        //todo: BU KALICI DEĞİL
        hotelID=1;
        airlineID=1;
        dateStart="18.12.2019";
        dateEnd="19.12.2019";

        TripDetail tripDetail = new TripDetail(1, airlineID, hotelID, dateStart, dateEnd);
        clientRequest(client, tripDetail);

        //server.stop();
    }

    private static void clientRequest(Client client, TripDetail tripDetail) {
        client.setFirstLoginFlag(false);
        client.startConnection(host, 8070);
        /*System.out.println("Client: " + tripDetail.numberOfTravellers + " " + tripDetail.preferredAirline + " "
                + tripDetail.preferredHotel + " " + tripDetail.dateStart + " " + tripDetail.dateEnd);*/
        String response=client.sendMessage(tripDetail, false);
        System.out.println("Server: \n" + response);
        client.stopConnection();
        confirmation(client, response); // confirmation method called. Its parameter is server response
    }

    private static void confirmation(Client client, String data) {
        String hotelID = data.substring(data.indexOf("Hotel-ID: ")+10, data.indexOf("Hotel-Suggestion:")-2);
        String airlineID = data.substring(data.indexOf("Airline-ID: ")+12, data.indexOf("Airline-Suggestion:")-2);
        // TODO: guiye tekrar gidecek okay veya cancel donecek. okay gelirse devam et, cancel gelirse bir sey yapmaya gerek yok
        client.setHotelID(Integer.parseInt(hotelID));
        client.setAirlineID(Integer.parseInt(airlineID));
        confirmationRequest(client);
    }

    private static void confirmationRequest(Client client) {
        client.startConnection(host, 8070);
        String response=client.sendMessage(new TripDetail(0, 0, 0, "", ""), true);
        System.out.println("Server: \n" + response);
        client.stopConnection();
    }

    public static void startServer() {
        server = new TravelAgency();
        serverThread = new Thread(server);
        serverThread.start();
    }
}
