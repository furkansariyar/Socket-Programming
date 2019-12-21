import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
    int hotelID, airlineID;
    HashMap<Integer, String> hotelsMap, airlinesMap;

    public GUI(HashMap<Integer, String> hotelsMap, HashMap<Integer, String> airlinesMap) {
        this.hotelsMap = hotelsMap;
        this.airlinesMap = airlinesMap;
        operation();
    }

    private void operation() {
        setLayout(new FlowLayout());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JComboBox hotelComboBox=new JComboBox(hotelsMap.values().toArray());
        JComboBox airlineComboBox=new JComboBox(airlinesMap.values().toArray());

        hotelComboBox.setBounds(50, 50,90,20);
        airlineComboBox.setBounds(50, 50,90,20);

        jPanel.add(hotelComboBox);
        jPanel.add(airlineComboBox);

        JLabel entranceDateLabel = new JLabel("Entrance Date: dd.mm.yyyy");
        JTextField entranceDate = new JTextField(16);

        jPanel.add(entranceDateLabel);
        jPanel.add(entranceDate);

        JLabel exitDateLabel = new JLabel("Exit Date: dd.mm.yyyy");
        JTextField exitDate = new JTextField(16);

        jPanel.add(exitDateLabel);
        jPanel.add(exitDate);

        add(jPanel);

        setSize(400,400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
