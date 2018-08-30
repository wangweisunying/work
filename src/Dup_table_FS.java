
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
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
public class Dup_table_FS implements TableCellRenderer {
    static JFrame frame;
    static JTable table;
    static JButton button1;
    static JButton button2;
    
    private static final Object[][] items={  
        {"1001","44","12",Boolean.FALSE},  
        {"1001","44","12",Boolean.FALSE},  
        {"1001","80","9",Boolean.FALSE},  
        {"1001","1","9",Boolean.FALSE},  
    };  
    private static final Object[] headers={"julien_barcode","current_unit(modify and don't check box)","previous_unit(modify and check box)","update to pre_unit?"};  
  
    public static void main(String[] args){  

    }
    
    

    public  void init(Object[][]items,Object[] headers){
//        try {  
//            //系统默认外观  
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
//        } catch (Exception ex) {  
//        }  
        frame=new JFrame("repeat sample details");  
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);  
        table = new JTable(items, headers){
                    
            @Override            
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                int modelRow = convertRowIndexToModel(row);
                int modelColumn = convertColumnIndexToModel(column);
                java.awt.Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(modelRow)) {
                    for(int i = 0;i<items.length;i++){
                        
                            if(modelRow ==i && modelColumn ==2&&Double.parseDouble(items[i][2].toString())>=10){
                                comp.setBackground(java.awt.Color.RED);
                                
                            }
                            else if((modelRow == i && modelColumn ==2)&&Double.parseDouble(items[i][2].toString())<10)
                            {
                                comp.setBackground(java.awt.Color.GREEN);
                                
                            }
                            else if(modelRow ==i && modelColumn ==3&&Double.parseDouble(items[i][3].toString())>=10){
                                comp.setBackground(java.awt.Color.RED);
                                
                            }
                            else if((modelRow == i && modelColumn ==3)&&Double.parseDouble(items[i][3].toString())<10) 
                            {
                                comp.setBackground(java.awt.Color.GREEN);
                                
                            }
                            else if(modelColumn!=2&&modelColumn!=3)
                            {
                                comp.setBackground(Color.WHITE);

                            } 

                        }
              
                    
                      
                }
                
              return comp; 
    }  
        };
                
                
                 
        
        button1 = new JButton("select All");
        button2 = new JButton("continue");
        
        button1.addActionListener(new ButtonAction());
        button2.addActionListener(new ButtonAction());
        
        
        TableColumn tc = table.getColumnModel().getColumn(4);
        tc.setCellEditor(table.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
        
        
//        for(int i = 0;i<table.getRowCount();i++){
//            for(int j = 0;j<table.getColumnCount();j++){
//                TableCellRenderer.getTableCellRendererComponent();
//            }
//        }
//            
        JScrollPane scroller=new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);  
        table.setPreferredScrollableViewportSize(new Dimension(900,600));
        
        frame.add(scroller);
        frame.add(button1);
        frame.add(button2);
        frame.setLayout(new FlowLayout());
        frame.setLocation(700,400);
        frame.setSize(1000, 800);
        frame.setVisible(true);
        
        //add  a buttion continue 
    
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class ButtonAction implements ActionListener{
        
        public void actionPerformed(ActionEvent ev){
            ArrayList<String> modi = new ArrayList();
            
            if(ev.getSource()==button1){
            //set all to true;
                int ct = 0;
                for (int j = 0; j < table.getRowCount(); j++) {
                    if(table.getModel().getValueAt(j,4)==Boolean.TRUE){
                        ct++;
                    }
                }  
            
                if(ct!=table.getRowCount()){
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.TRUE, j, 4);
                    } 
                }
                else{
                    for (int j = 0; j < table.getRowCount(); j++) {
                        table.setValueAt(Boolean.FALSE, j, 4);
                    } 
                }
                    
            }
            else if(ev.getSource() == button2){
                for(int i =0;i<table.getRowCount();i++){
                   modi.add(((String) table.getModel().getValueAt(i,0)).substring(0,10));//julien barcode
                   modi.add(((String) table.getModel().getValueAt(i,1)));//test_name
                   if(table.getModel().getValueAt(i,4)==Boolean.TRUE){
                       modi.add((String) table.getModel().getValueAt(i,3));
                   }
                   else{
                       modi.add((String) table.getModel().getValueAt(i,2));
                   }
                }
                   
                System.out.println(modi);          
                DataBaseTool.update_FS_dup(modi);
                frame.setVisible(false);
                frame.dispose();
            }
        }
    
    }
    
    
}
