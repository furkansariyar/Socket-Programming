
public class TripDetail {

    int numberOfTravellers;
    String preferredAirline, preferredHotel;

    public TripDetail(int numberOfTravellers, String preferredAirline, String preferredHotel) {
        this.numberOfTravellers = numberOfTravellers;
        this.preferredAirline = preferredAirline;
        this.preferredHotel = preferredHotel;
    }

    public int getNumberOfTravellers() {
        return numberOfTravellers;
    }

    public void setNumberOfTravellers(int numberOfTravellers) {
        this.numberOfTravellers = numberOfTravellers;
    }

    public String getPreferredAirline() {
        return preferredAirline;
    }

    public void setPreferredAirline(String preferredAirline) {
        this.preferredAirline = preferredAirline;
    }

    public String getPreferredHotel() {
        return preferredHotel;
    }

    public void setPreferredHotel(String preferredHotel) {
        this.preferredHotel = preferredHotel;
    }

    @Override
    public String toString() {
        return "TripDetail{" +
                "numberOfTravellers=" + numberOfTravellers +
                ", preferredAirline='" + preferredAirline + '\'' +
                ", preferredHotel='" + preferredHotel + '\'' +
                '}';
    }
}
