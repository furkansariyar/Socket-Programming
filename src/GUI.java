import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
    private int hotelID, airlineID, travellerCount;
    private String dateStart, dateEnd;
    private HashMap<Integer, String> hotelsMap, airlinesMap;
    Client client;

    public GUI(HashMap<Integer, String> hotelsMap, HashMap<Integer, String> airlinesMap, Client client) {
        this.hotelsMap = hotelsMap;
        this.airlinesMap = airlinesMap;
        this.client=client;
        //operation();
    }

    public void operation() {
        setLayout(new FlowLayout());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JComboBox hotelComboBox=new JComboBox(hotelsMap.values().toArray());
        JComboBox airlineComboBox=new JComboBox(airlinesMap.values().toArray());

        hotelComboBox.setBounds(50, 50,90,20);
        airlineComboBox.setBounds(50, 50,90,20);

        JLabel hotelLabel = new JLabel("Hotels");
        JLabel airlineLabel = new JLabel("Airlines");

        jPanel.add(hotelLabel);
        jPanel.add(hotelComboBox);
        jPanel.add(airlineLabel);
        jPanel.add(airlineComboBox);

        JLabel numberOfTravellersLabel = new JLabel("Number of Travellers");
        JTextField numberOfTravellersField = new JTextField(16);

        jPanel.add(numberOfTravellersLabel);
        jPanel.add(numberOfTravellersField);

        JLabel entranceDateLabel = new JLabel("Entrance Date: dd.mm.yyyy");
        JTextField entranceDate = new JTextField(16);

        jPanel.add(entranceDateLabel);
        jPanel.add(entranceDate);

        JLabel exitDateLabel = new JLabel("Exit Date: dd.mm.yyyy");
        JTextField exitDate = new JTextField(16);

        jPanel.add(exitDateLabel);
        jPanel.add(exitDate);

        JButton tripSearchButton = new JButton();
        tripSearchButton.setText("TRIP SEARCH");
        jPanel.add(tripSearchButton);

        add(jPanel);

        setSize(400,400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GUI gui = this;

        hotelComboBox.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getHotelIDFromString(hotelComboBox.getSelectedItem().toString());
            }
        });

        airlineComboBox.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAirlineIDFromString(airlineComboBox.getSelectedItem().toString());
            }
        });

        tripSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int checkCounter = 0;
                if (dateCheck(entranceDate.getText()))
                    checkCounter++;
                else
                    JOptionPane.showMessageDialog(null, "Entered an invalid date for entrance date!", "INVALID DATE", JOptionPane.ERROR_MESSAGE);

                if (dateCheck(exitDate.getText()))
                    checkCounter++;
                else
                    JOptionPane.showMessageDialog(null, "Entered an invalid date for exit date!", "INVALID DATE", JOptionPane.ERROR_MESSAGE);

                if (dateCompare((entranceDate.getText()), (exitDate.getText()))){
                    checkCounter++;
                }
                else
                    JOptionPane.showMessageDialog(null, "Entered an exit date that is before entrance date!", "INVALID DATE", JOptionPane.ERROR_MESSAGE);

                if (integerCheck(numberOfTravellersField.getText())){
                    checkCounter++;
                    if (numberOfTravellersCheck(Integer.parseInt(numberOfTravellersField.getText())))
                        checkCounter++;
                    else
                        JOptionPane.showMessageDialog(null, "Entered an invalid number for travellers!", "INVALID NUMBER OF TRAVELLERS", JOptionPane.ERROR_MESSAGE);
                }
                else
                    JOptionPane.showMessageDialog(null, "Entered an invalid number for travellers!", "INVALID NUMBER OF TRAVELLERS", JOptionPane.ERROR_MESSAGE);

                if (checkCounter==5){
                    getHotelIDFromString(hotelComboBox.getSelectedItem().toString());
                    getAirlineIDFromString(airlineComboBox.getSelectedItem().toString());
                    dateStart = entranceDate.getText();
                    dateEnd = exitDate.getText();
                    travellerCount = Integer.parseInt(numberOfTravellersField.getText());
                    Main.createClient(client,gui);
                }
            }
        });
    }

    public boolean dateCompare(String entranceStr, String exitStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (entranceStr.equals("") || exitStr.equals("")){
            return false;
        }

        try {
            Date entranceDate = dateFormat.parse(entranceStr);
            Date exitDate = dateFormat.parse(exitStr);
            if (exitDate.compareTo(entranceDate) > 1){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int responseConfirmation(Client client){
        int confirmationHotelID = client.getHotelID();
        int confirmationAirlineID = client.getAirlineID();
        String confirmationHotelName = hotelsMap.get(confirmationHotelID);
        String confirmationAirlineName = airlinesMap.get(confirmationAirlineID);
        String title = "";
        int result = -1;

        if (confirmationHotelID == 0 || confirmationAirlineID == 0){
            JOptionPane.showMessageDialog(null, "No trips were found to suit your wishes!", "TRIP NOT FOUND", JOptionPane.ERROR_MESSAGE);
        }
        else{
            if (confirmationHotelID == hotelID && confirmationHotelID == airlineID){
                title = "DESIRED TRIP CONFORMATION";
            }
            else{
                title = "SUGGESTION TRIP CONFIRMATION";
            }

            result = JOptionPane.showConfirmDialog(null,"Do you want to confirm this trip?\n" + "Hotel: " +
                            confirmationHotelName + "\n Airline: " + confirmationAirlineName, title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if(result == JOptionPane.YES_OPTION){
                JOptionPane.showMessageDialog(null, "You selected YES!", "CONFIRM", JOptionPane.INFORMATION_MESSAGE);
            }else if (result == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(null, "You selected NO!", "CONFIRM", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return result;
    }

    private boolean integerCheck(String number){
        try
        {
            int integer = Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    private boolean numberOfTravellersCheck(int number){
        if (number > 0){
            return true;
        }
        return false;
    }

    private boolean dateCheck(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try
        {
            dateFormat.parse(date);
        }
        /* Date format is invalid */
        catch (ParseException e)
        {
            return false;
        }
        return true;
    }

    private void getHotelIDFromString(String hotel) {
        // Find id of the hotel
        for (Map.Entry<Integer, String> entry:this.hotelsMap.entrySet()) {
            if (entry.getValue().equals(hotel)) {
                this.hotelID=entry.getKey();
            }
        }
    }

    private void getAirlineIDFromString(String airline) {
        for (Map.Entry<Integer, String> entry:this.airlinesMap.entrySet()) {
            if (entry.getValue().equals(airline)) {
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

    public void setAirlineIDID(int airlineID) {
        this.airlineID = airlineID;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getTravellerCount() {
        return travellerCount;
    }

    public void setTravellerCount(int travellerCount) {
        this.travellerCount = travellerCount;
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
