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


    public void getRequest() {
        while(true) {
            try {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                String response="";

                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    if (inputLine.equals("First-Login: true")) {

                        startClientConnection();    // start connection
                        sendInitialMessage();     // send message
                        stopClientConnection();

                        response += "Hotels: " + this.hotelsPart + "\r\n" + "Airlines: " + this.airlinesPart + "\r\n";
                        this.firstLoginFlag=true;
                        break;
                    }
                    else if (inputLine.equals("First-Login: false")) {

                        startClientConnection();
                        sendMessage(in.readLine());
                        stopClientConnection();

                        response += this.hotelsPart + "\r\n" + this.airlinesPart + "\r\n";
                        this.firstLoginFlag=false;
                    }
                    else if (inputLine.equals("Confirmation-Flag: true")) {
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println(in.readLine());
                        System.out.println(in.readLine());
                        System.out.println();
                        // TODO: 22.12.2019 send message to confirm the changes
                    }
                }
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

    @Override
    public void run() {
        System.out.println("Server is opening...");
        startHotelServer();
        startAirlineServer();
        start(8070);
    }

    public void startHotelServer() {
        hotelServer = new HotelController();
        hotelServerThread = new Thread(hotelServer);
        hotelServerThread.start();
    }

    public void startAirlineServer() {
        airlineServer = new AirlineController();
        airlineServerThread = new Thread(airlineServer);
        airlineServerThread.start();
    }

}
