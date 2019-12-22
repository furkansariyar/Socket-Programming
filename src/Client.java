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
    private int hotelID, airlineID;

    // open socket
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send message to travel agency over tcp socket
    // Used our new protocol that called NEW-PROTOCOL :)
    // Controlled with a flag to separate request message and confirmation message
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
            // Data comes from trip detail is converted to this format below.
            // And then putted in data header in protocol
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
            // This 'first login' header checks the message that initial message or not
            // If it is initial message, data header will be ignored in travel agency server
            out.println("First-Login: " + String.valueOf(this.firstLoginFlag));
            out.println("Data: " + message);
            out.println();
        }


        // Data comes from travel agency is putted in 'response' string
        String resp = null;
        String response = "";
        do {
            try {
                resp = in.readLine();
                if(!resp.isEmpty()) {
                    response += resp+"\r\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (resp!=null && !resp.isEmpty());
        return response; // return to main class
    }

    // closing socket
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
