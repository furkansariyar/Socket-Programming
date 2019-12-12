import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(TripDetail tripDetail) {

        String message = String.valueOf(tripDetail.numberOfTravellers)+"\r\n" +
                tripDetail.preferredAirline+"\r\n" +
                tripDetail.preferredHotel+"\r\n";

        out.println(message); // Sending message to Travel Agency server

        String resp = null;
        String response = "";
        do {
            try {
                resp = in.readLine();
                if(!resp.isEmpty()) {
                    response += resp+"\r\n";
                    System.out.println("------- " + resp);
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
}
