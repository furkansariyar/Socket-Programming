
public class TripDetail {

    int numberOfTravellers, preferredAirline, preferredHotel;

    public TripDetail(int numberOfTravellers, int preferredAirline, int preferredHotel) {
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

    public int getPreferredAirline() {
        return preferredAirline;
    }

    public void setPreferredAirline(int preferredAirline) {
        this.preferredAirline = preferredAirline;
    }

    public int getPreferredHotel() {
        return preferredHotel;
    }

    public void setPreferredHotel(int preferredHotel) {
        this.preferredHotel = preferredHotel;
    }

    @Override
    public String toString() {
        return "TripDetail{" +
                "numberOfTravellers=" + numberOfTravellers +
                ", preferredAirline=" + preferredAirline +
                ", preferredHotel=" + preferredHotel +
                '}';
    }
}
