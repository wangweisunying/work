
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class FoodGUI5 {

    JLabel label;
    JTextField well_plate_id;
    ArrayList<String> list = new ArrayList<>();
    JButton button;

    public void initFrame() {
        JFrame frame = new JFrame("FoodSensitivity_Contorl_Ver1.1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        label = new JLabel("Please input well_plate_id HERE>>>");

        well_plate_id = new JTextField("e.g. FOOD201712141", 20);
        well_plate_id.addMouseListener(new MouseAct());

        button = new JButton("Start");
        button.addActionListener(new ButtonAction());

        frame.getContentPane().add(label);
        frame.getContentPane().add(well_plate_id);
        frame.getContentPane().add(button);
        

        frame.setLayout(new FlowLayout());
        frame.setSize(1080,680);
        frame.setLocation(400,200);
        frame.setVisible(true);

    }

    class ButtonAction implements ActionListener {

        public void actionPerformed(ActionEvent ev) {
            try {
                UpdateTemplate5.run(UpdateTemplate5.getData_pillarId(
                        "select pillar_plate_id from `vibrant_test_raw_data`.`food_test` where exists (SELECT `pillar_plate_id` FROM `vibrant_test_tracking`.`pillar_plate_info` where substring(`vibrant_test_raw_data`.`food_test`.pillar_plate_id,1,18) = pillar_plate_id and well_plate_id = '"
                        + well_plate_id.getText() + "')  group by pillar_plate_id;"), well_plate_id.getText());
                button.setText(
                        "Sucess!check'C:\\Users\\Wei Wang\\Desktop\\work\\food sensitivity hamiltion clarity run' for the ouput ");
            } catch (Exception e) {
                button.setText("failed,Please check the input N reclick");
//                System.out.println(e);
            }

        }
    }

    class MouseAct implements MouseListener {

        public void mousePressed(MouseEvent e) {
            well_plate_id.setText("");
        }

        public void mouseClicked(MouseEvent e) {
            well_plate_id.setText("");

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }

    }

    public static void main(String[] args) {
        FoodGUI5 gui = new FoodGUI5();
        gui.initFrame();

    }


}
