import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AirlineController implements Runnable{
    private ServerSocket airlineSocket;
    private Socket agencySocket;
    private PrintWriter out;
    private BufferedReader in;
    private String host="127.0.0.1";

    public AirlineController() {
    }

    // open socket
    public void start(int port) {
        System.out.println("Airline server opened!\n");
        try {
            airlineSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRequest();
    }

    // wait request
    public void getRequest() {
        while (true) {
            try {
                agencySocket = airlineSocket.accept();
                out = new PrintWriter(agencySocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(agencySocket.getInputStream()));

                String inputLine;
                String request="";
                String response="";
                String data="";
                // read request line by line
                boolean checkAirlineFlag=false, confirmAirlineFlag=false;
                while ((inputLine=in.readLine()) != null && !inputLine.isEmpty()) {
                    request+=inputLine+"\r\n";
                }
                System.out.println(request);    // Displaying HTTP request content

                // there are 3 different scenario
                // determined these scenario at beginning of the headers
                String requestType = request.substring(request.indexOf(" /")+2, request.indexOf(" HTTP"));
                if (requestType.equals("getAllAirlines")) {
                    response += "Airlines: " +String.valueOf(DatabaseController.readFile(new File("Airlines.txt"))) + "\r\n";
                }
                else if (requestType.equals("checkAirlineSituation")) {
                    checkAirlineFlag=true;
                }
                else if (requestType.equals("confirmAirline")) {
                    confirmAirlineFlag=true;
                }
                else { }

                // trip request comes here
                // It checks airlines
                if (checkAirlineFlag) {
                    // parse data
                    ArrayList<Integer> airlineIDs = new ArrayList<Integer>();
                    data=request.substring(request.indexOf("AirlineID"), request.length()-2);
                    int airlineID=Integer.parseInt(data.substring(data.indexOf("AirlineID:")+11, data.indexOf("Target-HotelID:")-2));
                    // get proper airlines ids
                    airlineIDs=checkAirline(data);

                    if (airlineIDs.size()==0) {
                        // No available airline
                        response += "Airline-ID: " + 0 + "\r\n";
                        response += "Airline-Suggestion: false\r\n";
                    }
                    else if (airlineIDs.size()==1){
                        if (airlineID==airlineIDs.get(0)) {
                            // Requested airline is okay
                            response += "Airline-ID: " + airlineID + "\r\n";
                            response += "Airline-Suggestion: false\r\n";
                        }
                        else {
                            // Suggested airline. Just one
                            response += "Airline-ID: " + airlineIDs.get(0) + "\r\n";
                            response += "Airline-Suggestion: true\r\n";
                        }
                    }
                    else {
                        // Suggested airlines. More than one
                        response += "Airline-IDs: " + airlineIDs + "\r\n";
                        response += "Airline-Suggestion: true\r\n";
                    }
                }
                // Confirmation request comes here
                else if (confirmAirlineFlag) {
                    response+="Airline-Updated: "+confirmAirline(request.substring(request.indexOf("AirlineID:"), request.length()-2))+"\r\n";
                }

                // HTTP response headers
                out.println("HTTP/1.1 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: Airline Controller");
                out.println("Connection: close");
                out.println(response);

                // closing socket
                in.close();
                out.close();
                agencySocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // airline is updated with confirmation request
    private String confirmAirline(String request) {
        ArrayList<String> dates = new ArrayList<String>();
        // parse data
        int airlineID=Integer.parseInt(request.substring(request.indexOf("AirlineID:")+11, request.indexOf("Traveller-Count:")-2));
        int numberOfTraveller=Integer.parseInt(request.substring(request.indexOf("Traveller-Count:")+17, request.indexOf("Date-Start")-2));
        String dateStart=request.substring(request.indexOf("Date-Start:")+12, request.indexOf("Date-End:")-2);
        String dateEnd=request.substring(request.indexOf("Date-End:")+10, request.length());
        // just departure and arrival dates put in arraylist
        dates.add(dateStart);   // date of departure
        dates.add(dateEnd);     // date of arrival

        // update hotel file for each date
        for (int i=0; i<dates.size(); i++) {
            DatabaseController.updateFile(new File("AirlineDetail_"+airlineID+".txt"), dates.get(i), numberOfTraveller);
        }

        return "OK";
    }

    // check airline has enough free seat. Then return airlineID that has enough free seat.
    private ArrayList<Integer> checkAirline(String request) {
        HashMap<Integer, HashMap> data;
        HashMap<Integer, String> airlines;
        ArrayList<String> dates = new ArrayList<String>();
        // parse data
        int airlineID=Integer.parseInt(request.substring(request.indexOf("AirlineID:")+11, request.indexOf("Target-HotelID:")-2));
        int hotelID=Integer.parseInt(request.substring(request.indexOf("Target-HotelID:")+16, request.indexOf("Traveller-Count:")-2));
        int numberOfTraveller=Integer.parseInt(request.substring(request.indexOf("Traveller-Count:")+17, request.indexOf("Date-Start")-2));
        String dateStart=request.substring(request.indexOf("Date-Start:")+12, request.indexOf("Date-End:")-2);
        String dateEnd=request.substring(request.indexOf("Date-End:")+10, request.length());
        boolean airlineStatusFlag=true;
        ArrayList<Integer> airlineIDs = new ArrayList<Integer>();

        dates.add(dateStart);   // date of departure
        dates.add(dateEnd);     // date of arrival

        // check requested airline has enough free seat
        data = DatabaseController.readDetailFile(new File("AirlineDetail_"+airlineID+".txt"));
        airlineStatusFlag=checkTargetAndDates(dates, data, numberOfTraveller, hotelID);

        // if requested airline has enough free seat, then return id of the airline
        if (airlineStatusFlag) {
            airlineIDs.add(airlineID);
            return airlineIDs;
        }
        // if requested airline has not enough free seat, then return ids of the alternative airlines
        else {
            int alternativeAirlineID=1;
            airlines=DatabaseController.readFile(new File("Airlines.txt"));
            while (!airlineStatusFlag && alternativeAirlineID<=airlines.size()) {
                if (alternativeAirlineID!=airlineID) {
                    data = DatabaseController.readDetailFile(new File("AirlineDetail_"+alternativeAirlineID+".txt"));
                    if (airlineStatusFlag=checkTargetAndDates(dates, data, numberOfTraveller, hotelID)) {
                        airlineIDs.add(alternativeAirlineID);
                    }
                }
                alternativeAirlineID++;
            }
            return airlineIDs;
        }
    }

    // check airline has enough free seat in specified dates. Return true or false
    private boolean checkTargetAndDates(ArrayList <String> dates, HashMap<Integer, HashMap> data, int numberOfTraveller, int hotelID) {
        int freeSeat;
        // we have 2 dates so there are 2 flags for each dates. In each match, just one flag set to 'true'
        boolean flag1=false;
        boolean flag2=false;
        for (int i=0; i<dates.size(); i++) {
            for (int j=1; j<=data.size(); j++) {    // id starts from 1 in databases
                if (dates.get(i).equals(data.get(j).get("Date"))) {
                    if (Integer.parseInt((String) data.get(j).get("Target"))==hotelID) {
                        freeSeat=Integer.parseInt((String) data.get(j).get("Capacity")) - Integer.parseInt((String) data.get(j).get("Engaged"));
                        if (numberOfTraveller <= freeSeat) {
                            if (flag1) {
                                flag2=true;
                            }
                            else {
                                flag1=true;
                            }
                        }
                        j=data.size()+1;
                    }
                }
            }
        }
        return flag1 && flag2;
    }

    // thread is running and airline controller behaves as a server
    @Override
    public void run() {
        System.out.println("Airline server is opening...");
        start(8090);
    }
}
