import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class AirlineController implements Runnable{
    private ServerSocket airlineSocket;
    private Socket agencySocket;
    private PrintWriter out;
    private BufferedReader in;

    private String host="127.0.0.1";
    // TODO: ports can be defined here

    public AirlineController() {
    }

    public void start(int port) {
        System.out.println("Airline server opened!\n");
        try {
            airlineSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRequest();
    }

    public void getRequest() {
        while (true) {
            try {
                agencySocket = airlineSocket.accept();
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
                if (requestType.equals("getAllAirlines")) {
                    //response += "Hotels: " +String.valueOf(DatabaseController.readFile(new File("Hotels.txt"))) + "\r\n";
                    response += "Airlines: " +String.valueOf(DatabaseController.readFile(new File("Airlines.txt"))) + "\r\n";
                }
                else {
                    //todo: trip detail request i buraya gelecek
                    //response+="FURKAN\r\n";
                }

                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Airline Controller");
                out.println("Connection: close");
                out.println(response);

                in.close();
                out.close();
                agencySocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Airline server is opening...");
        start(8090);
    }
}
