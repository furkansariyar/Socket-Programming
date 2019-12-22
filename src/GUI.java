import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        jPanel.add(hotelComboBox);
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
                if (dateCheck(entranceDate.getText()) && dateCheck(exitDate.getText()) && integerCheck(numberOfTravellersField.getText())){
                    getHotelIDFromString(hotelComboBox.getSelectedItem().toString());
                    getAirlineIDFromString(airlineComboBox.getSelectedItem().toString());
                    dateStart = entranceDate.getText();
                    dateEnd = exitDate.getText();
                    travellerCount = Integer.parseInt(numberOfTravellersField.getText());
                    Main.createClient(client,gui);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Entered a invalid date for!", "InfoBox: " + "INVALID DATE", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void responseConfirmation(Client client){
        int confirmationHotelID = client.getHotelID();
        int confirmationAirlineID = client.getAirlineID();
        String confirmationHotelName = hotelsMap.get(confirmationHotelID);
        String confirmationAirlineName = hotelsMap.get(confirmationAirlineID);

        int result = JOptionPane.showConfirmDialog(null,"Do you want to confirm this trip?\n" + "Hotel: " +
                        confirmationHotelName + "\n Airline: " + confirmationAirlineName, "TRIP CONFIRM",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION){
            JOptionPane.showMessageDialog(null, "You selected YES!", "InfoBox: " + "CONFIRM", JOptionPane.INFORMATION_MESSAGE);
        }else if (result == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "You selected NO!", "InfoBox: " + "CONFIRM", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean integerCheck(String number){
        try
        {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
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
