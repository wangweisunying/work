/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wei Wang
 */
public class ALPSUI implements TableCellRenderer {

    static JFrame frame;
    static JTable table;
    static JButton selectAll;
    static JButton recalculate;
    static JButton insert;
    static JLabel posRateG, posRateA , log , caliG , caliA;
    static JTextField cfG, cfA;
    static HashMap<String, Unit> map = new HashMap();
    
    public static void main(String[] args) {
        ALPSUI table = new ALPSUI();
    }

    public void init(Object[][] items, Object[] headers, String PosG, String PosA , double cfG_v , double cfA_v , HashMap<String, Unit> x) {
        map = x;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        frame = new JFrame("ALPS  details");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        table = new JTable(items, headers) {

            @Override
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                int modelRow = convertRowIndexToModel(row);
                int modelColumn = convertColumnIndexToModel(column);
                java.awt.Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(modelRow)) {
                    for (int i = 0; i < items.length; i++) {

                        if ((String) items[i][0] == "ALPSIGA_unit" && modelRow == i && modelColumn == 2 && Double.parseDouble(items[i][2].toString()) >= 30) {
                            comp.setBackground(java.awt.Color.RED);
                        }
                        else if((String) items[i][0] == "ALPSIGA_unit" && modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) >= 30){
                            comp.setBackground(java.awt.Color.RED);
                        }
                        else if ((String) items[i][0] == "ALPSIGA_unit" && modelRow == i && modelColumn == 2 && Double.parseDouble(items[i][2].toString()) < 30) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }
                        else if ((String) items[i][0] == "ALPSIGA_unit" && modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) < 30) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }else if ((String) items[i][0] == "ALPSIGG_IGM_unit" && modelRow == i && modelColumn == 2 && Double.parseDouble(items[i][2].toString()) >= 280) {
                            comp.setBackground(java.awt.Color.RED);
                        }else if ((String) items[i][0] == "ALPSIGG_IGM_unit" && modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) >= 280) {
                            comp.setBackground(java.awt.Color.RED);
                        }
                        else if ((String) items[i][0] == "ALPSIGG_IGM_unit" && modelRow == i && modelColumn == 2 && Double.parseDouble(items[i][2].toString()) < 280) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }
                        else if ((String) items[i][0] == "ALPSIGG_IGM_unit" && modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) < 280) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }
                        else if (modelColumn != 2 && modelColumn != 3) {
                            comp.setBackground(Color.WHITE);

                        }

                    }

                }

                return comp;
            }
        };

        selectAll = new JButton("select All");
        recalculate = new JButton("recalculate");
        insert = new JButton("Insert to DB");

        selectAll.addActionListener(new ButtonAction());
        recalculate.addActionListener(new ButtonAction());
        insert.addActionListener(new ButtonAction());

        posRateG = new JLabel("ALPS IGG POSITIVE RATE ------ " + PosG + " ----Not Satisfy? change cfG here ---> ");
        posRateA = new JLabel("ALPS IGA POSITIVE RATE ------ " + PosA + " ----Not Satisfy? change cfA here ---> ");
  
        
//        System.out.println("A1"+ map.get("A1G").unit);
//        System.out.println("B1"+ map.get("B1G").unit);
//        System.out.println("C1"+ map.get("C1G").unit);
//        System.out.println("D1"+ map.get("D1G").unit);
//        System.out.println("G12"+ map.get("G12G").unit);
//        
//        
        
        caliG = new JLabel("IGG details : A1: " + map.get("A1G").unit + " B1: " + map.get("B1G").unit + " C1: " + map.get("C1G").unit + " D1: " + map.get("D1G").unit + " G12: " + map.get("G12G").unit);
        caliA = new JLabel("IGA details : A1: " + map.get("A1A").unit + " B1: " + map.get("B1A").unit + " C1: " + map.get("C1A").unit + " D1: " + map.get("D1A").unit + " G12: " + map.get("G12A").unit);
        
        cfG = new JTextField( String.valueOf(cfG_v) , 5);
        cfA = new JTextField( String.valueOf(cfA_v), 5);
        
        log = new JLabel("");
        
        TableColumn tc = table.getColumnModel().getColumn(4);
        tc.setCellEditor(table.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));

//        for(int i = 0;i<table.getRowCount();i++){
//            for(int j = 0;j<table.getColumnCount();j++){
//                TableCellRenderer.getTableCellRendererComponent();
//            }
//        }
//     
        JScrollPane scroller = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(posRateG);
        frame.getContentPane().add(cfG);
        frame.getContentPane().add(caliG);
        frame.getContentPane().add(posRateA);
        frame.getContentPane().add(cfA);
        frame.getContentPane().add(caliA);
        

        frame.getContentPane().add(scroller);
        frame.getContentPane().add(selectAll);
        frame.getContentPane().add(recalculate);
        frame.getContentPane().add(insert);
        frame.getContentPane().add(log);
            
        frame.setLayout(new FlowLayout());
        frame.setLocation(700, 400);
        frame.setSize(800, 800);
        frame.setVisible(true);

        //add  a buttion continue 
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class ButtonAction implements ActionListener {

        public void actionPerformed(ActionEvent ev) {
            if (ev.getSource() == selectAll) {
                //set all to true;
                int ct = 0;
                for (int j = 0; j < table.getRowCount(); j++) {
                    if (table.getModel().getValueAt(j, 4) == Boolean.TRUE) {
                        ct++;
                    }
                }

                if (ct != table.getRowCount()) {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.TRUE, j, 4);
                    }
                } else {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.FALSE, j, 4);
                    }
                }

            } else if (ev.getSource() == recalculate) {
                double GG = Double.parseDouble(cfG.getText()) , AA = Double.parseDouble(cfA.getText());   
                frame.dispose();
                ALPS alps  = new ALPS();
                alps.run( GG , AA );
                
            } else if (ev.getSource() == insert){ 
                HashMap<String,Double>  modiMap = new HashMap();
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (table.getModel().getValueAt(i, 4) == Boolean.TRUE) {
                        modiMap.put((String) table.getModel().getValueAt(i , 5),Double.parseDouble(table.getModel().getValueAt(i , 2).toString()));
                    } else {
                        modiMap.put((String) table.getModel().getValueAt(i , 5),Double.parseDouble(table.getModel().getValueAt(i , 3).toString()));
                    }
                }
                ALPS alps  = new ALPS();
                System.out.println(modiMap);
                alps.insertDB(modiMap , map);
                log.setText("Done ! go ahead n check lis");
                
            }
                
            
        }

    }

}

