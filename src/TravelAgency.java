import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class TravelAgency implements Runnable {

    /* TODO: Oteller ve havayolu şirketleriyle iletişim kurulacak.
     * TODO: istenilen tarih aralığında, kişi sayısı da göz önünde bulundurularak yer var mı sorgulanacak.
     * */

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private HashMap<Integer, String> hotels;
    private HashMap<Integer, String> airlines;
    private boolean firstLoginFlag;


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
                    response+=inputLine+"\r\n";
                    //System.out.println("******* " + inputLine);
                    if (inputLine.equals("First-Login: true")) {
                        //todo: burası yanlış. otel ve airline db lerine buradan erişim sağlanamaz.
                        //todo: tcp soketleri üzerinden http ile otel ve airline db lerine bağlanılacak.
                        // get all hotels and airlines from db, and put them in response
                        this.hotels=DatabaseController.readFile(new File("Hotels.txt")) ;
                        this.airlines=DatabaseController.readFile(new File("Airlines.txt")) ;
                        response += "Hotels: " + this.hotels.toString() + "\r\n" +
                                "Airlines: " + this.airlines.toString() + "\r\n";
                        this.firstLoginFlag=true;
                        break;
                    }
                    else if (inputLine.equals("First-Login: false")) {
                        this.firstLoginFlag=false;
                        break;
                    }
                }
                response += "Status-code: 200\r\n";

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

    @Override
    public void run() {
        System.out.println("Server is opening...");
        start(6666);
    }

}
