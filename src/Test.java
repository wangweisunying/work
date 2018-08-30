
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComboBox;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wei Wang
 */
public class Test {

    static JFrame jf;
    static JButton bt;//food sen
    static JButton bt1;//ibs
    static JComboBox cb;
    static JTextField well_plate_id;
    static JLabel jl;

    //for Food Sensitivity DuplicationCheckUP
    static JButton fdButton;

    //for Food Sensitivity Repeat Samples FoodOverWrite
    static JButton frButton;

    public static void main(String args[]) {
        Test x = new Test();
        x.init();
    }

    private static void init() {

        jf = new JFrame("Vibrant Sciences Data Tool ver 1.0");
        bt = new JButton("Start Analysis - " + "Food Sensitivity");
        cb = new JComboBox();
        jl = new JLabel("log");

        String checkdup = "Check & Update Duplicate_";
        String overwrite = "overwrite the repe at_";

        fdButton = new JButton(checkdup + "Food Sensitivity");
        frButton = new JButton(overwrite + "Food Sensitivity");
        fdButton.addActionListener(new ButtonAction());
        frButton.addActionListener(new ButtonAction());

        well_plate_id = new JTextField("Please input food wellplate id", 20);
        well_plate_id.addMouseListener(new MouseAct());

        bt1 = new JButton("Ready to go - " + "Food Sensitivity");
        bt.addActionListener(new ButtonAction());
        bt1.addActionListener(new ButtonAction());

        cb.addItem("Food Sensitivity");
        cb.addItem("IBS");
        cb.addItem("Food Allergy");
        cb.addItem("Neurology");
        cb.addItem("ALPS");
        cb.addItem("Zonulin");

        cb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                switch (event.getStateChange()) {
                    case ItemEvent.SELECTED:
                        well_plate_id.setText("please input " + event.getItem() + "WellPlateID");
                        bt.setText("Start Analysis - " + event.getItem());
                        bt1.setText("Ready to go - " + event.getItem());
                        System.out.println("selected" + event.getItem());

                        if (event.getItem() == "Food Sensitivity") {
                            fdButton.setText(checkdup + "Food Sensitivity");
                            frButton.setText(overwrite + "Food Sensitivity");

                            jf.add(fdButton);
                            jf.add(frButton);

                        } //                        else if (event.getItem()=="Zonulin"){
                        //                            fdButton.setText(checkdup+"Zonulin");
                        //                            frButton.setText(overwrite+"Zonulin");
                        //                            jf.add(fdButton);
                        //                            jf.add(frButton);
                        //                        
                        //                        }
                        else {
                            jf.remove(fdButton);
                            jf.remove(frButton);
                        }

                        break;
                    case ItemEvent.DESELECTED:
                        System.out.println("deselected" + event.getItem());
                        break;
                }
            }
        });

        jf.add(cb);
        jf.add(well_plate_id);
        jf.add(bt);
        jf.add(bt1);
        jf.add(jl);

        jf.add(fdButton);
        jf.add(frButton);

        jf.setLayout(new FlowLayout());
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(1000, 500);
        jf.setLocation(600, 200);
        jf.setVisible(true);

    }

    private static class ButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {

            if ((ev.getSource() == bt)) {//start analysis
                switch (bt.getText().substring(17)) {
                    case "Food Sensitivity":
//get the data out      
                        try {
                            System.out.println("select pillar_plate_id from `vibrant_test_raw_data`.`food_test` where exists (SELECT `pillar_plate_id` FROM `vibrant_test_tracking`.`pillar_plate_info` where `vibrant_test_raw_data`.`food_test`.pillar_plate_id = pillar_plate_id and well_plate_id = '" + well_plate_id.getText() + "')  group by pillar_plate_id;");
                            UpdateTemplate5.run(UpdateTemplate5.getData_pillarId(
                                    "select pillar_plate_id from `vibrant_test_raw_data`.`food_test` where exists (SELECT `pillar_plate_id` FROM `vibrant_test_tracking`.`pillar_plate_info` where `vibrant_test_raw_data`.`food_test`.pillar_plate_id = pillar_plate_id  and status = 'finish' and well_plate_id = '"
                                    + well_plate_id.getText() + "')  group by pillar_plate_id;"), well_plate_id.getText());
                            jl.setText("success!");
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage() + ex.getClass());
                            jl.setText("wellplate id is not in database,please recheck");
                        }

                        System.out.println("Food Sensitivity");
                        break;
                    case "IBS":
                        System.out.println("IBS");
                        break;
                    case "Food Allergy":
                        try {
                            FoodAllergy_UpdateTmp.run(FoodAllergy_UpdateTmp.getData_pillarId(
                                    "select pillar_plate_id from `vibrant_test_raw_data`.`food_test` where exists (SELECT `pillar_plate_id` FROM `vibrant_test_tracking`.`pillar_plate_info` where substring(`vibrant_test_raw_data`.`food_test`.pillar_plate_id,1,18) = pillar_plate_id and well_plate_id = '"
                                    + well_plate_id.getText().substring(0, 18) + "')  group by pillar_plate_id;"), well_plate_id.getText().substring(0, 18));
                            jl.setText("success!" + well_plate_id.getText().substring(0, 18));
                        } catch (Exception ex) {
                            jl.setText("wellplate id is not in database,please recheck");
                        }

                        System.out.println(well_plate_id.getText().substring(0, 18));
                        break;
                    case "Neurology":
                        System.out.println("Neurology");
                        break;
                    case "ALPS":
                        System.out.println("ALPS");
                        break;

                    case "Zonulin":
                        System.out.println("Zonulin");
                        //
                        try {
                            ExcelTool.updateZig(DataBaseTool.getZigData(DataBaseTool.serverInfo("V7"), well_plate_id.getText()));
                            jl.setText("success!");
                        } catch (Exception ex) {

                            jl.setText("wellplate id is not in database,please recheck");
                        }

                        ///
                        break;
                    //add more test here
                }
            } //Ready to go button
            else if ((ev.getSource() == bt1)) { //insert unit to db
                switch (bt1.getText().substring(14)) {
                    case "Food Sensitivity":
// write the Excel to DataBase 

                        System.out.println(well_plate_id.getText());
                        try {

                            //write igg
                            ExceltoDataBase.readExceltoDb("g", well_plate_id.getText());
                            //write iga
                            ExceltoDataBase.readExceltoDb("a", well_plate_id.getText());

                            //pop out the jtable
                            DataBaseTool.getFoodDup(well_plate_id.getText());

                            //popout failed samples
                            FailedSampleUI fail_ui = new FailedSampleUI(24, well_plate_id.getText());
                            fail_ui.setFailed_init();

                            jl.setText("ready to Go! " + well_plate_id.getText() + "please go n check lis");

                        } catch (Exception ex) {
                            jl.setText("something is wrong...  " + ex.getMessage());

                        }

                        break;
                    case "IBS":
                        System.out.println("IBS");
                        break;
                    case "Food Allergy":
                        System.out.println("Food Allergy");
                        break;
                    case "Neurology":
                        System.out.println("Neurology");
                        break;
                    case "ALPS":
                        System.out.println("ALPS");
                        break;

                    case "Zonulin":
                        System.out.println("Zonulin to db");

                        try {
                            DataBaseTool.checkDupZigData(ExcelTool.readZigExcel(well_plate_id.getText()), DataBaseTool.getZigDup());

                            jl.setText("data inserted! please go to lis");
                        } catch (Exception ex) {
                            jl.setText("something is wrong...  " + ex.getMessage());
                        }
                        break;

                    //add more test here
                }

            } //check duplication and update thedata
            else if ((ev.getSource() == fdButton)) {

                switch (fdButton.getText()) {
                    case "Check & Update Duplicate_Food Sensitivity":
//                FoodDupCheck.modifyDup(well_plate_id.getText());
//                jl.setText("finish modifying dup data");

                        break;
                    case "Check & Update Duplicate_Zonulin":

                        break;

                }
            } //overwrite the repeat sample
            else if ((ev.getSource() == frButton)) {
                switch (frButton.getText()) {
                    case "overwrite the repeat_Food Sensitivity":

                        break;
                    case "overwrite the repeat_Zonulin":

                        break;

                }

//need failed repeat julienBarcode;  make a GUI to check which one fail chips
            }

        }
    }

    private static class MouseAct implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {
            well_plate_id.setText("");
        }

        @Override
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

}
