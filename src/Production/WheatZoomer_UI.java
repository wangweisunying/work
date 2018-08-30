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
import java.util.ArrayList;
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
public class WheatZoomer_UI implements TableCellRenderer {

    static JFrame frame;
    static JTable table;
    static JButton selectAll;
    static JButton recalculate;
    static JButton insert;
    static JLabel log;
    static JLabel[] Qc ;
    static JTextField[] QcText ;
    static JLabel[] test_lable = new JLabel[40];
    static JTextField[] test_cf = new JTextField[40];
    static HashMap<String ,DBData> map = new HashMap();
    static HashMap<String ,DBData> original_data = new HashMap();
    static HashMap<String , WZ_Dup_info> dupUnit = new HashMap();
    static HashMap<String , String> pre_curMap  = new HashMap();


    public void init(Object[][] items, Object[] headers, HashMap<String ,DBData> dbData ,String[] PosRate , double[] cf , HashMap<String , WZ_Dup_info> dupinfo , HashMap<String , String> preCurMap , HashMap<String ,DBData> origin ,ArrayList<String> negative) {
        map = dbData;
        original_data = origin;
        dupUnit = dupinfo;
        pre_curMap = preCurMap;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        frame = new JFrame("WZ  details");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        table = new JTable(items, headers) {

            @Override
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                int modelRow = convertRowIndexToModel(row);
                int modelColumn = convertColumnIndexToModel(column);
                java.awt.Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(modelRow)) {
                    for (int i = 0; i < items.length; i++) {

                        if ( modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) >= 0.9) {
                            comp.setBackground(java.awt.Color.RED);
                        }
                        else if(modelRow == i && modelColumn == 4 && Double.parseDouble(items[i][4].toString()) >= 0.9){
                            comp.setBackground(java.awt.Color.RED);
                        }
                        else if (modelRow == i && modelColumn == 3 && Double.parseDouble(items[i][3].toString()) < 0.9) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }
                        else if (modelRow == i && modelColumn == 4 && Double.parseDouble(items[i][4].toString()) < 0.9) {
                            comp.setBackground(java.awt.Color.GREEN);
                        }
                        else if (modelColumn != 3 && modelColumn != 4) {
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

        //get the label
        WheatZoomer_New wz = new WheatZoomer_New();
        Parameter para = new Parameter();
        for(int i = 0 ; i < wz.test_name.length ; i ++){
            
            test_lable[i] = new JLabel(wz.test_name[i] + "posRate = " + PosRate[i] +"  Change here -->");
//            test_cf[i] = new JTextField(String.valueOf(cf[i]) , 5);
            test_cf[i] = new JTextField(String.format("%.2f" , cf[i]) , 5); 
            double curPos = Double.parseDouble(PosRate[i]);
            if(i % 2 == 0){//igg
                if( curPos > para.igg_range[1]){
                    test_lable[i].setForeground(Color.red);
                }
                else if(curPos < para.igg_range[0]){
                    test_lable[i].setForeground(Color.blue);
                }
            }
            else{//iga
                if( curPos > para.iga_range){
                    test_lable[i].setForeground(Color.red);
                }
            }
            
            
            
        }
        StringBuilder sb = new StringBuilder();
        for(String tmp : negative){
            sb.append(tmp + " ");
        }
        // show cali pos_ctrl neg_ctrl
        

        
        Qc = new JLabel[para.exclude_julienBarcode.length];
        QcText = new JTextField[para.exclude_julienBarcode.length];
        
        
        System.out.println(dbData);
        for(String key : dbData.keySet()){
            if(key == null){
                continue;
            }
            for(int i = 0 ; i < Qc.length ; i ++){
                
                if(key.startsWith(para.exclude_julienBarcode[i])){
                    Qc[i] = new JLabel(para.exclude_julienBarcode[i]);
                    QcText[i] = new JTextField(String.valueOf(dbData.get(key).unit_arr[39]).substring(0 , 4), 5);           
                }
            }
        }
        
        
        log = new JLabel("negative: "+sb.toString());
        
        
        TableColumn tc = table.getColumnModel().getColumn(5);
        tc.setCellEditor(table.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
        
        
        
        

        JScrollPane scroller = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        //set size
//        table.setSize(550, 300);
//        scroller.setViewportView(table);
        
        for(int i = 0 ; i < test_lable.length ; i ++){
            frame.getContentPane().add(test_lable[i]);
            frame.getContentPane().add(test_cf[i]);
        }
        
        frame.getContentPane().add(scroller);
        frame.getContentPane().add(selectAll);
        frame.getContentPane().add(recalculate);
        frame.getContentPane().add(insert);
        for(int i = 0 ; i < Qc.length ; i ++){
            frame.getContentPane().add(Qc[i]);
            frame.getContentPane().add(QcText[i]);  
        }
        frame.getContentPane().add(log);
        
        frame.setLayout(new FlowLayout());
        frame.setLocation(700, 0);
        frame.setSize(630, 1200);
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
                    if (table.getModel().getValueAt(j, 5) == Boolean.TRUE) {
                        ct++;
                    }
                }

                if (ct != table.getRowCount()) {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.TRUE, j, 5);
                    }
                } else {
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.FALSE, j, 5);
                    }
                }

            } else if (ev.getSource() == recalculate) {
                
                
                double[] modi_cf = new double[test_cf.length];
                for(int i = 0 ; i < modi_cf.length ; i ++){
                    modi_cf[i] = Double.parseDouble(test_cf[i].getText());
                }
                frame.dispose();
                WheatZoomer_New wz = new WheatZoomer_New();
                wz.reshowUI(original_data , dupUnit , modi_cf ,pre_curMap);
                
                
            } else if (ev.getSource() == insert){ 
                String[] QcTextRs = new String[QcText.length];
                for(int i  = 0 ; i < QcTextRs.length ; i ++){
                    QcTextRs[i] = QcText[i].getText();
                }

                
                HashMap<String,Double>  modiMap = new HashMap();
                for (int i = 0; i < table.getRowCount(); i++) {
                    String julienBarcode = (String) table.getModel().getValueAt(i , 0);
                    
                    if (table.getModel().getValueAt(i, 5) == Boolean.TRUE) {
                        modiMap.put(julienBarcode.substring(0 , 10) + String.valueOf(table.getModel().getValueAt(i , 2)).split("-")[0] , Double.parseDouble(table.getModel().getValueAt(i , 3).toString())); 
                    } else {
                        modiMap.put(julienBarcode.substring(0 , 10) + String.valueOf(table.getModel().getValueAt(i , 2)).split("-")[0] , Double.parseDouble(table.getModel().getValueAt(i , 4).toString()));
                    }
                }
                WheatZoomer_New wz  = new WheatZoomer_New();
                System.out.println(modiMap);
                wz.insertDB(modiMap , map , QcTextRs);
                log.setText("Done ! go ahead n check lis");
                
            }
                
            
        }

    }

}

