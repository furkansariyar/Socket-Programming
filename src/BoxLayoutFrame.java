import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class BoxLayoutFrame extends JFrame {

    public BoxLayoutFrame(HashMap<Integer, String> hotelsMap, HashMap<Integer, String> airlinesMap) {
        setLayout(new FlowLayout());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JComboBox hotelComboBox=new JComboBox(hotelsMap.values().toArray());
        JComboBox airlineComboBox=new JComboBox(airlinesMap.values().toArray());
        hotelComboBox.setBounds(50, 50,90,20);
        airlineComboBox.setBounds(50, 50,90,20);

        jPanel.add(hotelComboBox);
        jPanel.add(airlineComboBox);

        add(jPanel);

        setSize(400,400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
