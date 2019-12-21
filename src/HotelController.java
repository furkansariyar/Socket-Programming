import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
                String data="";
                boolean checkHotelFlag=false;
                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    request+=inputLine+"\r\n";
                }
                System.out.println(request);    // Displaying HTTP request content

                String requestType = request.substring(request.indexOf(" /")+2, request.indexOf(" HTTP"));
                if (requestType.equals("getAllHotels")) {
                    response += "Hotels: " +String.valueOf(DatabaseController.readFile(new File("Hotels.txt"))) + "\r\n";
                }
                else if(requestType.equals("checkHotelSituation")) {
                    checkHotelFlag=true;
                }
                else {
                    System.out.println("YASAK BÖLGE");
                }

                if (checkHotelFlag) {
                    ArrayList<Integer> hotelIDs = new ArrayList<Integer>();
                    data=request.substring(request.indexOf("HotelID"), request.length()-2);
                    int hotelID=Integer.parseInt(data.substring(data.indexOf("HotelID:")+9, data.indexOf("Traveller-Count:")-2));
                    hotelIDs=checkHotel(data);

                    if (hotelIDs.size()==0) {
                        // No available hotel
                        response += "Hotel ID: " + 0 + "\r\n";
                        response += "Hotel Suggestion: false\r\n";
                    }
                    else if (hotelIDs.size()==1){
                        if (hotelID==hotelIDs.get(0)) {
                            // Requested hotel is okay
                            response += "Hotel ID: " + hotelID + "\r\n";
                            response += "Hotel Suggestion: false\r\n";
                        }
                        else {
                            // Suggested hotel. Just one
                            response += "Hotel ID: " + hotelID + "\r\n";
                            response += "Hotel Suggestion: true\r\n";
                        }
                    }
                    else {
                        // Suggested hotels. More than one
                        response += "Hotel IDs: " + hotelIDs + "\r\n";
                        response += "Hotel Suggestion: true\r\n";
                    }
                }

                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Hotel Controller");
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

    // todo: return alicaz
    private ArrayList<Integer> checkHotel(String request) {
        HashMap<Integer, HashMap> data;
        HashMap<Integer, String> hotels;
        ArrayList<String> dates = new ArrayList<String>();
        int hotelID=Integer.parseInt(request.substring(request.indexOf("HotelID:")+9, request.indexOf("Traveller-Count:")-2));
        int numberOfTraveller=Integer.parseInt(request.substring(request.indexOf("Traveller-Count:")+17, request.indexOf("Date-Start")-2));
        String dateStart=request.substring(request.indexOf("Date-Start:")+12, request.indexOf("Date-End:")-2);
        String dateEnd=request.substring(request.indexOf("Date-End:")+10, request.length());
        int startDate_day=Integer.parseInt(dateStart.substring(0,2));
        boolean hotelStatusFlag=true;
        ArrayList<Integer> hotelIDs = new ArrayList<Integer>();

        // todo: ayni aydaki gunlerin farkini aliyor sadece(BUG)
        while (startDate_day<=Integer.parseInt(dateEnd.substring(0,2))) {
            dates.add(String.valueOf(startDate_day)+dateStart.substring(2, dateStart.length()));
            startDate_day++;
        }

        data = DatabaseController.readDetailFile(new File("HotelDetail_"+hotelID+".txt"));
        //System.out.println("*************************");
        //System.out.println(data);

        hotelStatusFlag=checkDates(dates, data, numberOfTraveller);

        if (hotelStatusFlag) {
            // TODO: hotel uygundur response
            hotelIDs.add(hotelID);
            return hotelIDs;
        }
        else {
            int alternativeHotelID=1;
            hotels=DatabaseController.readFile(new File("Hotels.txt"));
            while (!hotelStatusFlag && alternativeHotelID<=hotels.size()) {
                if (alternativeHotelID!=hotelID) {
                    data = DatabaseController.readDetailFile(new File("HotelDetail_"+alternativeHotelID+".txt"));
                    if (hotelStatusFlag=checkDates(dates, data, numberOfTraveller)) {
                        hotelIDs.add(alternativeHotelID);
                        //break;
                    }
                }
                alternativeHotelID++;
            }
            //System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq " + alternativeHotelID);
            return hotelIDs;
        }
    }

    private boolean checkDates(ArrayList <String> dates, HashMap<Integer, HashMap> data, int numberOfTraveller) {
        int freeRoom;
        boolean hotelStatusFlag=true;
        for (String date: dates) {
            for (int i:data.keySet()) {
                if (date.equals(data.get(i).get("Date"))) {
                    freeRoom=Integer.parseInt((String) data.get(i).get("Capacity")) - Integer.parseInt((String) data.get(i).get("Full Room"));
                    if (numberOfTraveller > freeRoom) {
                        hotelStatusFlag=false;
                        //System.out.println("YER YOK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }
            }
        }
        return hotelStatusFlag;
    }

    @Override
    public void run() {
        System.out.println("Hotel server is opening...");
        start(8080);
    }
}
