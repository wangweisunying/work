
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wei Wang
 */
public class FailedSampleUI {

    private int row;
    private int col;
    private static String well_id;
    private static JFrame jf = new JFrame("select failed samples");
    public static ArrayList<String> list = new ArrayList();
    

    public FailedSampleUI(int well_type,String well_id) {
        this.jf.getContentPane().removeAll();
        this.list.clear();
        switch (well_type) {
            case 24:
                this.well_id = well_id;
                this.row = 4;
                this.col = 6;
                
                jf.setSize(350,200);
                break;

            case 96:
                this.well_id = well_id;
                this.row = 8;
                this.col = 12;
               
                jf.setSize(700,350);
                break;
        }
    }
    public static void main(String[] args){
           FailedSampleUI ui = new FailedSampleUI(24,"123");
           ui.setFailed_init();
        
    }
    
    public void setFailed_init() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                JButton button = new JButton();
                button.setSize(50,50);
                button.setText(Character.toString((char) (i + 65)) + String.valueOf(j + 1));
                button.addActionListener(new ButtonAction());
                jf.getContentPane().add(button);
                
//                System.out.println(Character.toString((char) (i + 65)) + String.valueOf(j + 1));
            }
        }
        JButton contin = new JButton();
        contin.addActionListener(new ButtonAction());
        contin.setText("continue");
        jf.getContentPane().add(contin);
        jf.setLayout(new FlowLayout());
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);   
    }

    private static class ButtonAction implements ActionListener {
        
        public void actionPerformed(ActionEvent ev) {
//            System.out.println(ev.getActionCommand());
            if(ev.getActionCommand()!="continue"){
                JButton source = (JButton)ev.getSource();
                if(source.getBackground()==java.awt.Color.red){
                    source.setBackground(java.awt.Color.WHITE);
                    list.remove(ev.getActionCommand());
                }else{
                    source.setBackground(java.awt.Color.red);
                    list.add(ev.getActionCommand());
                }     
                
            }else{
                System.out.println(list);
                DataBaseTool.update_FS_fail(list,well_id);
                jf.setVisible(false);
            }        
        }
    }

}
