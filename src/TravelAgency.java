import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TravelAgency implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket, agencySocket_Hotel, agencySocket_Airline;
    private PrintWriter out, out2, out3;
    private BufferedReader in, in2, in3;
    private boolean firstLoginFlag;
    private String host="127.0.0.1";
    private static HotelController hotelServer;
    private static AirlineController airlineServer;
    private static Thread hotelServerThread, airlineServerThread;
    String hotelsPart="", airlinesPart="";

    public TravelAgency() {
    }

    // open socket
    public void start(int port) {
        System.out.println("Server opened!\n");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRequest();
    }

    public void startClientConnection() {
        try {
            // Hotel Socket
            agencySocket_Hotel = new Socket(this.host, 8080);
            out2 = new PrintWriter(agencySocket_Hotel.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(agencySocket_Hotel.getInputStream()));

            // Airline Socket
            agencySocket_Airline = new Socket(this.host, 8090);
            out3 = new PrintWriter(agencySocket_Airline.getOutputStream(), true);
            in3 = new BufferedReader(new InputStreamReader(agencySocket_Airline.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInitialMessage() {
        try {
            // HTTP request methods for getting all hotels and airlines
            // First one is for hotel
            // Second one is for airlines
            out2.println("GET " + "/getAllHotels" + " HTTP/1.1");
            out2.println("Host: " + this.host);
            out2.println("User-Agent: Travel Agency");
            out2.println("Accept: text/html");
            out2.println("Accept-Language: en-US");
            out2.println("Connection: close");
            out2.println();

            out3.println("GET " + "/getAllAirlines" + " HTTP/1.1");
            out3.println("Host: " + this.host);
            out3.println("User-Agent: Travel Agency");
            out3.println("Accept: text/html");
            out3.println("Accept-Language: en-US");
            out3.println("Connection: close");
            out3.println();

            // Getting HTTP response from hotels and airlines controllers, below
            String inputLineHotel, inputLineAirline;
            String responseHotel="", responseAirline="";
            while ((inputLineHotel=in2.readLine()) != null && !inputLineHotel.isEmpty()) {
                responseHotel+=inputLineHotel+"\r\n";
            }
            System.out.println(responseHotel);    // Displaying HTTP response content

            while ((inputLineAirline=in3.readLine()) != null && !inputLineAirline.isEmpty()) {
                responseAirline+=inputLineAirline+"\r\n";
            }
            System.out.println(responseAirline);    // Displaying HTTP response content

            if (responseHotel.contains("Hotels:")) {
                this.hotelsPart = responseHotel.substring(responseHotel.indexOf("Hotels: ")+8, responseHotel.indexOf("}")+1);
            }

            if (responseAirline.contains("Airlines:")) {
                this.airlinesPart = responseAirline.substring(responseAirline.indexOf("Airlines: ")+10, responseAirline.indexOf("}")+1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String data) {
        String[] dataArr=data.substring(6, data.length()).split(","); // First trim the 'Data: ' part, then split
        int numberOfTravellers = Integer.parseInt(dataArr[0]);
        int airlineID = Integer.parseInt(dataArr[1]);
        int hotelID = Integer.parseInt(dataArr[2]);
        String dateStart = dataArr[3];
        String dateEnd = dataArr[4];

        try {
            // HTTP request methods for trip request
            // First one is for hotel
            // Second one is for airlines
            out2.println("GET " + "/checkHotelSituation" + " HTTP/1.1");
            out2.println("Host: " + this.host);
            out2.println("User-Agent: Travel Agency");
            out2.println("Accept: text/html");
            out2.println("Accept-Language: en-US");
            out2.println("Connection: close");
            out2.println("HotelID: " + hotelID);
            out2.println("Traveller-Count: " + numberOfTravellers);
            out2.println("Date-Start: " + dateStart);
            out2.println("Date-End: " + dateEnd);
            out2.println();

            out3.println("GET " + "/checkAirlineSituation" + " HTTP/1.1");
            out3.println("Host: " + this.host);
            out3.println("User-Agent: Travel Agency");
            out3.println("Accept: text/html");
            out3.println("Accept-Language: en-US");
            out3.println("Connection: close");
            out3.println("AirlineID: " + airlineID);
            out3.println("Target-HotelID: " + hotelID);
            out3.println("Traveller-Count: " + numberOfTravellers);
            out3.println("Date-Start: " + dateStart);
            out3.println("Date-End: " + dateEnd);
            out3.println();

            // Getting HTTP response from hotels and airlines controllers, below
            String inputLineHotel, inputLineAirline;
            String responseHotel="", responseAirline="";
            while ((inputLineHotel=in2.readLine()) != null && !inputLineHotel.isEmpty()) {
                responseHotel+=inputLineHotel+"\r\n";
            }
            System.out.println(responseHotel);    // Displaying HTTP response content

            while ((inputLineAirline=in3.readLine()) != null && !inputLineAirline.isEmpty()) {
                responseAirline+=inputLineAirline+"\r\n";
            }
            System.out.println(responseAirline);    // Displaying HTTP response content

            // Check responses what includes.
            // They may include all hotels and airlines.
            // They may include hotel and airline IDs that proper for preferred hotels and airlines. It used for trip request
            // They may includes alternative hotel and airline IDs. It used for trip request.
            if (responseHotel.contains("Hotels:")) {
                this.hotelsPart = responseHotel.substring(responseHotel.indexOf("Hotels: ")+8, responseHotel.indexOf("}")+1);
            }

            if (responseAirline.contains("Airlines:")) {
                this.airlinesPart = responseAirline.substring(responseAirline.indexOf("Airlines: ")+10, responseAirline.indexOf("}")+1);
            }

            if (responseHotel.contains("Hotel-ID:")) {
                this.hotelsPart = "Hotel-ID: " + responseHotel.substring(responseHotel.indexOf("Hotel-ID: ")+10, responseHotel.indexOf("Hotel-Suggestion:")-2) + "\r\n";
                this.hotelsPart += "Hotel-Suggestion: " + responseHotel.substring(responseHotel.indexOf("Hotel-Suggestion: ")+18, responseHotel.length()-2);
            }

            if (responseAirline.contains("Airline-ID:")) {
                this.airlinesPart = "Airline-ID: " + responseAirline.substring(responseAirline.indexOf("Airline-ID: ")+12, responseAirline.indexOf("Airline-Suggestion:")-2) + "\r\n";
                this.airlinesPart += "Airline-Suggestion: " + responseAirline.substring(responseAirline.indexOf("Airline-Suggestion: ")+20, responseAirline.length()-2);
            }

            if (responseHotel.contains("Hotel-IDs:")) {
                this.hotelsPart = "Hotel-IDs: " + responseHotel.substring(responseHotel.indexOf("Hotel-IDs: ")+11, responseHotel.indexOf("Hotel-Suggestion:")-2) + "\r\n";
                this.hotelsPart += "Hotel-Suggestion: " + responseHotel.substring(responseHotel.indexOf("Hotel-Suggestion: ")+18, responseHotel.length()-2);
            }

            if (responseAirline.contains("Airline-IDs:")) {
                this.airlinesPart = "Airline-IDs: " + responseAirline.substring(responseAirline.indexOf("Airline-IDs: ")+13, responseAirline.indexOf("Airline-Suggestion:")-2) + "\r\n";
                this.airlinesPart += "Airline-Suggestion: " + responseAirline.substring(responseAirline.indexOf("Airline-Suggestion: ")+20, responseAirline.length()-2);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sending confirmation request to hotels and airlines controller
    public void sendConfirmationMessage(String hotelPart, String airlinePart, String dateStartPart, String dateEndPart, String numberOfTraveller) {
        // parsing data
        int hotelID = Integer.parseInt(hotelPart.substring(hotelPart.indexOf("Hotel-ID: ")+10, hotelPart.length()));
        int airlineID = Integer.parseInt(airlinePart.substring(airlinePart.indexOf("Airline-ID: ")+12, airlinePart.length()));
        String dateStart = dateStartPart.substring(dateStartPart.indexOf("Date-Start: ")+12, dateStartPart.length());
        String dateEnd = dateEndPart.substring(dateEndPart.indexOf("Date-End: ")+10, dateEndPart.length());
        int travellerCount = Integer.parseInt(numberOfTraveller.substring(numberOfTraveller.indexOf("Traveller-Count: ")+17), numberOfTraveller.length());

        try {
            // HTTP request methods for confirmation
            // First one is for hotel
            // Second one is for airlines

            out2.println("GET " + "/confirmHotel" + " HTTP/1.1");
            out2.println("Host: " + this.host);
            out2.println("User-Agent: Travel Agency");
            out2.println("Accept: text/html");
            out2.println("Accept-Language: en-US");
            out2.println("Connection: close");
            out2.println("HotelID: " + hotelID);
            out2.println("Traveller-Count: " + travellerCount);
            out2.println("Date-Start: " + dateStart);
            out2.println("Date-End: " + dateEnd);
            out2.println();

            out3.println("GET " + "/confirmAirline" + " HTTP/1.1");
            out3.println("Host: " + this.host);
            out3.println("User-Agent: Travel Agency");
            out3.println("Accept: text/html");
            out3.println("Accept-Language: en-US");
            out3.println("Connection: close");
            out3.println("AirlineID: " + airlineID);
            out3.println("Traveller-Count: " + travellerCount);
            out3.println("Date-Start: " + dateStart);
            out3.println("Date-End: " + dateEnd);
            out3.println();

            // Getting HTTP response from hotels and airlines controllers, below
            String inputLineHotel, inputLineAirline;
            String responseHotel="", responseAirline="";
            while ((inputLineHotel=in2.readLine()) != null && !inputLineHotel.isEmpty()) {
                responseHotel+=inputLineHotel+"\r\n";
            }
            System.out.println(responseHotel);    // Displaying HTTP response content

            while ((inputLineAirline=in3.readLine()) != null && !inputLineAirline.isEmpty()) {
                responseAirline+=inputLineAirline+"\r\n";
            }
            System.out.println(responseAirline);    // Displaying HTTP response content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // wait request
    public void getRequest() {
        while(true) {
            try {
                clientSocket = serverSocket.accept(); // match client socket
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                String response="";

                // There are 3 different scenario
                // first login: true means get all hotels and airlines. We do not need other data
                // first login: false means that trip detail request
                // confirmation flag true means that we need to update databases
                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    if (inputLine.equals("First-Login: true")) {
                        startClientConnection();    // start connection
                        sendInitialMessage();     // send initial message. It gets all hotels and airlines
                        stopClientConnection();
                        // hotels and airlines are putted in response
                        response += "Hotels: " + this.hotelsPart + "\r\n" + "Airlines: " + this.airlinesPart + "\r\n";
                        this.firstLoginFlag=true;
                        break;
                    }
                    else if (inputLine.equals("First-Login: false")) {
                        startClientConnection();
                        // we just need a data located in header after 'first login' header
                        sendMessage(in.readLine());
                        stopClientConnection();
                        // after trip request, hotelID and airlineID are putted in response
                        response += this.hotelsPart + "\r\n" + this.airlinesPart + "\r\n";
                        this.firstLoginFlag=false;
                    }
                    else if (inputLine.equals("Confirmation-Flag: true")) {
                        // after confirmation flag, hotelID, airlineID, dateStart, dateEnd and travellerCount headers comes respectively
                        // this headers sent to 'sendConfirmationMessage' method to update
                        startClientConnection();
                        sendConfirmationMessage(in.readLine(), in.readLine(), in.readLine(), in.readLine(), in.readLine());
                        response += "Update Status: Successful\r\n";
                        stopClientConnection();
                        break;
                    }
                }
                // HTTP response headers
                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Travel Agency");
                out.println("Connection: close");
                out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // stop server socket
    public void stop() {
        try {
            System.out.println("\nServer closed");
            stopClientConnection(); // Closing client connection too.
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // stop client connection
    public void stopClientConnection() {
        try {
            // Close Hotel Socket
            in2.close();
            out2.close();
            agencySocket_Hotel.close();
            // Close Airline Socket
            in3.close();
            out3.close();
            agencySocket_Airline.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // thread is running and travel agency behaves as a server
    // Call 2 functions that will start hotelServer and AirlineServer
    @Override
    public void run() {
        System.out.println("Server is opening...");
        startHotelServer();
        startAirlineServer();
        start(8070);
    }

    // start hotel server
    public void startHotelServer() {
        hotelServer = new HotelController();
        hotelServerThread = new Thread(hotelServer);
        hotelServerThread.start();
    }

    // start airline server
    public void startAirlineServer() {
        airlineServer = new AirlineController();
        airlineServerThread = new Thread(airlineServer);
        airlineServerThread.start();
    }

}
