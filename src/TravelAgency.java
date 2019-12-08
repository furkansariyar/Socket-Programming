import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TravelAgency implements Runnable {

    /* TODO: Oteller ve havayolu şirketleriyle iletişim kurulacak.
     * TODO: istenilen tarih aralığında, kişi sayısı da göz önünde bulundurularak yer var mı sorgulanacak.
     * */

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TravelAgency() {
    }

    public void start(int port) {
        System.out.println("Server opened!\n");
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String response="";
            while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                response+=inputLine+"\r\n";
                System.out.println("******* " + inputLine);
            }
            out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void run() {
        System.out.println("Server is opening...");
        start(6666);
    }

}
