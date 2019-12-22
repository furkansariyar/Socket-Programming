import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean firstLoginFlag;
    private String host="127.0.0.1";
    // TODO: ports can be defined here
    private int hotelID, airlineID;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(TripDetail tripDetail, boolean confirmationFlag) {

        if (confirmationFlag) {
            out.println("NEW-PROTOCOL/1.1");
            out.println("Host: " + this.host);
            out.println("User-Agent: Client");
            out.println("Accept: text/html");
            out.println("Accept-Language: en-US");
            out.println("Confirmation-Flag: true");
            out.println("Hotel-ID: "+ this.hotelID);
            out.println("Airline-ID: "+ this.airlineID);
            out.println();
        }
        else {
            String message = String.valueOf(tripDetail.numberOfTravellers)+"," +
                    tripDetail.preferredAirline+"," +
                    tripDetail.preferredHotel+"," +
                    tripDetail.dateStart+"," +
                    tripDetail.dateEnd+"\r\n";

            out.println("NEW-PROTOCOL/1.1");
            out.println("Host: " + this.host);
            out.println("User-Agent: Client");
            out.println("Accept: text/html");
            out.println("Accept-Language: en-US");
            out.println("First-Login: " + String.valueOf(this.firstLoginFlag));
            out.println("Data: " + message); // Sending message to Travel Agency server
            out.println();
        }

        String resp = null;
        String response = "";
        do {
            try {
                resp = in.readLine();
                if(!resp.isEmpty()) {
                    response += resp+"\r\n";
                    //System.out.println("------- " + resp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (resp!=null && !resp.isEmpty());
        return response;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstLoginFlag() {
        return this.firstLoginFlag;
    }

    public void setFirstLoginFlag(boolean firstLoginFlag) {
        this.firstLoginFlag = firstLoginFlag;
    }

    public void setHotelID(int hotelID) {
        this.hotelID = hotelID;
    }

    public void setAirlineID(int airlineID) {
        this.airlineID = airlineID;
    }
}
