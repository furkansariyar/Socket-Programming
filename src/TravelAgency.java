import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class TravelAgency implements Runnable {

    /* TODO: Oteller ve havayolu şirketleriyle iletişim kurulacak.
     * TODO: istenilen tarih aralığında, kişi sayısı da göz önünde bulundurularak yer var mı sorgulanacak.
     * */

    private ServerSocket serverSocket;
    private Socket clientSocket,agencySocket;
    private PrintWriter out,out2;
    private BufferedReader in,in2;

/*    private HashMap<Integer, String> hotels;
    private HashMap<Integer, String> airlines;*/
    private boolean firstLoginFlag;
    private String host="127.0.0.1";
    // TODO: ports can be defined here

    private static HotelController hotelServer;
    private static Thread hotelServerThread;

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
            agencySocket = new Socket(this.host, 6667);
            out2 = new PrintWriter(agencySocket.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(agencySocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //getRequest();
    }

    public void sendInitialMessage() {
        try {
            //out2.println("############# initial message\r\n"); // todo: http request

            out2.println("GET " + "/getAllHotelsAndAirlines" + " HTTP/1.1");
            out2.println("Host: " + this.host);
            out2.println("User-Agent: Travel Agency");
            out2.println("Accept: text/html");
            out2.println("Accept-Language: en-US");
            out2.println("Connection: close");
            out2.println();

            String inputLine;
            String response="";
            while ((inputLine=in2.readLine()) != null && !inputLine.isEmpty()) {
                response+=inputLine+"\r\n";
            }
            System.out.println(response);    // Displaying HTTP response content

            if (response.contains("Hotels:")) {
                this.hotelsPart = response.substring(response.indexOf("Hotels: ")+8, response.indexOf("}")+1);
                this.airlinesPart = response.substring(response.indexOf("Airlines: ")+10, response.length()-2);
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
            in2.close();
            out2.close();
            agencySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Server is opening...");
        startHotelServer();
        start(6666);
    }

    public void startHotelServer() {
        hotelServer = new HotelController();
        hotelServerThread = new Thread(hotelServer);
        hotelServerThread.start();
    }

}
