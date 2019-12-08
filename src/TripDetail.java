import java.util.ArrayList;

public class TripDetail {

    int numberOfTravellers;
    ArrayList<String> preferredAirlines = new ArrayList<String>();
    ArrayList<String> preferredHotels = new ArrayList<String>();

    public TripDetail(int numberOfTravellers, ArrayList<String> preferredAirlines, ArrayList<String> preferredHotels) {
        this.numberOfTravellers = numberOfTravellers;
        this.preferredAirlines = preferredAirlines;
        this.preferredHotels = preferredHotels;
    }

    public int getNumberOfTravellers() {
        return numberOfTravellers;
    }

    public void setNumberOfTravellers(int numberOfTravellers) {
        this.numberOfTravellers = numberOfTravellers;
    }

    public ArrayList<String> getPreferredAirlines() {
        return preferredAirlines;
    }

    public void setPreferredAirlines(ArrayList<String> preferredAirlines) {
        this.preferredAirlines = preferredAirlines;
    }

    public ArrayList<String> getPreferredHotels() {
        return preferredHotels;
    }

    public void setPreferredHotels(ArrayList<String> preferredHotels) {
        this.preferredHotels = preferredHotels;
    }

    @Override
    public String toString() {
        return "TripDetail{" +
                "numberOfTravellers=" + numberOfTravellers +
                ", preferredAirlines=" + preferredAirlines +
                ", preferredHotels=" + preferredHotels +
                '}';
    }
}
