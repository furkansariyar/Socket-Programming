import java.net.*;
import java.io.*;
import java.util.Date;

public class HotelController implements Runnable{

    private ServerSocket hotelSocket;
    private Socket agencySocket;
    private PrintWriter out;
    private BufferedReader in;

    private String host="127.0.0.1";
    // TODO: ports can be defined here

    public HotelController() {
    }

    public void start(int port) {
        System.out.println("Hotel server opened!\n");
        try {
            hotelSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRequest();
    }

    public void getRequest() {
        while (true) {
            try {
                agencySocket = hotelSocket.accept();
                out = new PrintWriter(agencySocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(agencySocket.getInputStream()));

                String inputLine;
                String request="";
                String response="";
                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    request+=inputLine+"\r\n";
                }
                System.out.println(request);    // Displaying HTTP request content

                String requestType = request.substring(request.indexOf(" /")+2, request.indexOf(" HTTP"));
                if (requestType.equals("getAllHotelsAndAirlines")) {
                    response += "Hotels: " +String.valueOf(DatabaseController.readFile(new File("Hotels.txt"))) + "\r\n";
                    response += "Airlines: " +String.valueOf(DatabaseController.readFile(new File("Airlines.txt"))) + "\r\n";
                }
                else {
                    //todo: trip detail request i buraya gelecek
                }

                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Hotel Controller");
                out.println("Connection: close");
                out.println(response);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Hotel server is opening...");
        start(6667);
    }
}
