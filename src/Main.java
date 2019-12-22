import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    private static TravelAgency server;
    private static Thread serverThread;
    private static String host="127.0.0.1";

    public static void main(String[] args) {
        startServer();  // start Travel Agency as a server
        Client client = createAndstartClientConnection(); // create client and start create connection
        String response = getAllHotelsAndAirlines(client); // get all hotels and airlines from database
        // gui started after get all hotels and airlines
        GUI gui = new GUI(parseFirstResponse(response).get(0), parseFirstResponse(response).get(1), client);
        gui.operation(); // start gui
    }

    // Create Client object and start connection with server
    public static Client createAndstartClientConnection(){
        Client client = new Client();   // create client object
        client.startConnection(host, 8070); // start connection with server
        client.setFirstLoginFlag(true); // set first login flag true
        return client;
    }

    // Get All hotels and Airlines
    public static String getAllHotelsAndAirlines(Client client){
        // trip data does not matter for first login
        // first login flag is important here, we set it true above
        String response=client.sendMessage(new TripDetail(0, 0, 0, "", ""), false);
        System.out.println("Server:\n" + response);
        client.stopConnection();
        return response;
    }

    // parsing operation for all hotels and all airlines
    public static List<HashMap<Integer, String>> parseFirstResponse(String response) {
        // Get hotels part. It contains all hotels with their ids and their names
        String hotelsPart = response.substring(response.indexOf("Hotels: ")+9, response.indexOf("}"));
        // Get airlines part. It contains all airlines with their ids and their names
        String airlinesPart = response.substring(response.indexOf("Airlines: ")+11, response.length()-3);
        // We use hashmap for mapping hotels and airlines
        HashMap<Integer, String> hotelsMap = new HashMap<Integer, String>();
        HashMap<Integer, String> airlinesMap = new HashMap<Integer, String>();
        hotelsMap.put(1, hotelsPart.substring(2, hotelsPart.indexOf(",")));
        hotelsMap.put(2, hotelsPart.substring(hotelsPart.indexOf("2")+2, hotelsPart.length()));
        airlinesMap.put(1, airlinesPart.substring(2, airlinesPart.indexOf(",")));
        airlinesMap.put(2, airlinesPart.substring(airlinesPart.indexOf("2")+2, airlinesPart.length()));
        // Maplist contains hotels map and airlines map
        List<HashMap<Integer, String>> mapList = new ArrayList<HashMap<Integer, String>>();
        mapList.add(hotelsMap);
        mapList.add(airlinesMap);
        return mapList;
    }

    public static void createClient(Client client, GUI gui) {
        // new trip detail object with data comes from gui
        TripDetail tripDetail = new TripDetail(gui.getTravellerCount(), gui.getAirlineID(), gui.getHotelID(), gui.getDateStart(), gui.getDateEnd());
        // create trip request for client with trip detail and gui objects
        clientRequest(client, tripDetail, gui);
    }

    private static void clientRequest(Client client, TripDetail tripDetail, GUI gui) {
        client.setFirstLoginFlag(false);    // we need to set first login false when do trip connection
        client.startConnection(host, 8070); // start connection
        String response=client.sendMessage(tripDetail, false);
        System.out.println("Server: \n" + response);
        client.stopConnection();    // stop connection
        // After getting response trip request, we need to get confirmation from user
        // confirmation method called. Its parameter is server response
        confirmation(client, response, gui, tripDetail);
    }

    private static void confirmation(Client client, String data, GUI gui, TripDetail tripDetail) {
        // Getting hotelID, airlineID from response data
        String hotelID = data.substring(data.indexOf("Hotel-ID: ")+10, data.indexOf("Hotel-Suggestion:")-2);
        String airlineID = data.substring(data.indexOf("Airline-ID: ")+12, data.indexOf("Airline-Suggestion:")-2);
        // Set client's hotelID and airlineID
        client.setHotelID(Integer.parseInt(hotelID));
        client.setAirlineID(Integer.parseInt(airlineID));

        // if user confirm the trip then sending confirmation request
        // 0 means accept, 1 denied
        // if user denied, there is no need to do.
        if (gui.responseConfirmation(client) == 0){
            confirmationRequest(client, tripDetail);
        }
    }

    // start client connection and sending confirm request
    private static void confirmationRequest(Client client, TripDetail tripDetail) {
        client.startConnection(host, 8070);
        String response=client.sendMessage(tripDetail, true);
        System.out.println("Server: \n" + response);
        client.stopConnection();
    }

    // start Travel Agency as a server using thread
    public static void startServer() {
        server = new TravelAgency();
        serverThread = new Thread(server);
        serverThread.start();
    }
}
