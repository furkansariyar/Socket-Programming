import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TravelAgency implements Runnable {

    /* TODO: Oteller ve havayolu şirketleriyle iletişim kurulacak.
     * TODO: istenilen tarih aralığında, kişi sayısı da göz önünde bulundurularak yer var mı sorgulanacak.
     * */

    private ServerSocket serverSocket;
    private Socket clientSocket, agencySocket_Hotel, agencySocket_Airline;
    private PrintWriter out, out2, out3;
    private BufferedReader in, in2, in3;

/*    private HashMap<Integer, String> hotels;
    private HashMap<Integer, String> airlines;*/
    private boolean firstLoginFlag;
    private String host="127.0.0.1";
    // TODO: ports can be defined here

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


    public void getRequest() {
        while(true) {
            try {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //this.firstLoginFlag=false;
                String inputLine;
                String response="";

                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    //response+=inputLine+"\r\n";
                    //System.out.println("******* " + inputLine);
                    if (inputLine.equals("First-Login: true")) {

                        startClientConnection();    // start connection
                        sendInitialMessage();     // send message
                        stopClientConnection();

                        response += "Hotels: " + this.hotelsPart + "\r\n" +
                                "Airlines: " + this.airlinesPart + "\r\n";
                        this.firstLoginFlag=true;
                        break;
                    }
                    else if (inputLine.equals("First-Login: false")) {
                        this.firstLoginFlag=false;
                        break;
                    }
                }
                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Travel Agency");
                out.println("Connection: close");

                if (!firstLoginFlag) {
                    out.println(response);
                }
                else {
                    out.println(response);
                }

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
