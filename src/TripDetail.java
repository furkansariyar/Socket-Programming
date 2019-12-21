
public class TripDetail {

    int numberOfTravellers, preferredAirline, preferredHotel;
    String dateStart, dateEnd;

    public TripDetail(int numberOfTravellers, int preferredAirline, int preferredHotel, String dateStart, String dateEnd) {
        this.numberOfTravellers = numberOfTravellers;
        this.preferredAirline = preferredAirline;
        this.preferredHotel = preferredHotel;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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

    public String getDataStart() {
        return dateStart;
    }

    public void setDataStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String toString() {
        return "TripDetail{" +
                "numberOfTravellers=" + numberOfTravellers +
                ", preferredAirline=" + preferredAirline +
                ", preferredHotel=" + preferredHotel +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                '}';
    }
}
