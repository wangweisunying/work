/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Veryfication;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author Wei Wang
 */
public class FS_verify {
    public static void main(String args[]){
        
        int lotNumber=5;
 
        ArrayList<ArrayList<Double>> list = new ArrayList();
        ArrayList<ArrayList<Double>> listA = new ArrayList();
        for(int i = 0;i<100;i++){
            ArrayList listx = new ArrayList();
            list.add(listx);
            listA.add(listx);
        }
       
        String query = "select `index`,`signal` from vibrant_test_raw_data.food_test where pillar_plate_id like'FOOG800"+lotNumber+"%' and concat(row,col) in ('C6','D6');";
        String queryA = "select `index`,`signal` from vibrant_test_raw_data.food_test where pillar_plate_id like'FOOA800"+lotNumber+"%' and concat(row,col) in ('C6','D6');";
        
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int x = Integer.parseInt(rs.getString("index"));
                list.get(x).add(Double.parseDouble(rs.getString("signal")));
            }
            
            
            ResultSet rsA = stmt.executeQuery(queryA);
            while(rsA.next()){
                int x = Integer.parseInt(rsA.getString("index"));
                listA.get(x).add(Double.parseDouble(rsA.getString("signal")));
            }
           
            
            
            
            
        //write to excel
            XSSFWorkbook wk = new XSSFWorkbook();
            XSSFSheet sheet = wk.createSheet("IGG");
            XSSFSheet sheetA = wk.createSheet("IGA");
            
            for(int i=0;i<list.size();i++){
                Row row = sheet.createRow(i);
                for(int j=0;j<list.get(i).size();j++){
                    row.createCell(j).setCellValue(list.get(i).get(j));
                }  
            }
            for(int i=0;i<listA.size();i++){
                Row row = sheetA.createRow(i);
                for(int j=0;j<listA.get(i).size();j++){
                    row.createCell(j).setCellValue(listA.get(i).get(j));
                }  
            }
            
            FileOutputStream out = new FileOutputStream("C:\\Users\\Wei Wang\\Desktop\\work\\FOOD QC\\lot verification\\lot"+lotNumber+".xlsx");
            wk.write(out);
            wk.close();
            out.close();
            
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
        
       
    
    }
}
