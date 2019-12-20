import java.util.HashMap;
import java.util.Map;

public class GUI {
    int hotelID, airlineID;
    HashMap<Integer, String> hotelsMap, airlinesMap;

    public GUI(HashMap<Integer, String> hotelsMap, HashMap<Integer, String> airlinesMap) {
        this.hotelsMap = hotelsMap;
        this.airlinesMap = airlinesMap;
        operation();
    }

    private void operation() {
        // TODO: GUI operations
        getIDFromString(); // if it is necessary
    }

    // Get ID of the hotel/airline from its text
    private void getIDFromString() {
        // Find id of the hotel
        for (Map.Entry<Integer, String> entry:this.hotelsMap.entrySet()) {
            if (entry.getValue().equals("otel erzincan")) {
                this.hotelID=entry.getKey();
            }
        }
        // Find id of the airline
        for (Map.Entry<Integer, String> entry:this.airlinesMap.entrySet()) {
            if (entry.getValue().equals("erzincan turizm")) {
                this.airlineID=entry.getKey();
            }
        }
    }

    public int getHotelID() {
        return hotelID;
    }

    public void setHotelID(int hotelID) {
        this.hotelID = hotelID;
    }

    public int getAirlineID() {
        return airlineID;
    }

    public void setAirlineID(int airlineID) {
        this.airlineID = airlineID;
    }

    @Override
    public String toString() {
        return "GUI{" +
                "hotelID=" + hotelID +
                ", airlineID=" + airlineID +
                ", hotelsMap=" + hotelsMap +
                ", airlinesMap=" + airlinesMap +
                '}';
    }
}
