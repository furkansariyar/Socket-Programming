import java.net.*;
import java.io.*;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
/*
    public Server() {
    }

    public void start(int port) {
        System.out.println("Server opened!\n");
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }
                out.println(inputLine);
            }

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
    }*/

/*    public static void main(String[] args) {
        System.out.println("Server opened");
        Server server=new Server();
        server.start(6666);
    }*/

    @Override
    public void run() {
        System.out.println("Server is opening...");
        //start(6666);
    }
}
