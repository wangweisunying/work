
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wei Wang
 */
public class ExceltoDataBase {

    public static void main(String args[]) {
        try {
            readExceltoDb("g","food201801252_20180126"); //read g read a or read both
        } catch (Exception ex) {
            Logger.getLogger(ExceltoDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public static void insertLis(ArrayList<ArrayList<String>> list,String gOrA) {
//        try {
//            Connection myConn = DriverManager
//                    .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
//            Statement stmt = myConn.createStatement();
//
//            for (int i = 0; i < 97; i++) {
//                for (int j = 0; j < 22; j++) {
//                    stmt.executeUpdate("insert into tsp_test_unit_data.foo"+gOrA+"_unit_data (test_name,julien_barcode,unit, pillar_plate_id,row,col) values('"
//                            + list.get(0).get(i) + "','" + list.get(1).get(j) + "','" + list.get(2).get(i * 22 + j) + "','" + list.get(1).get(22) + "','" + (char) (j % 4 + 65) + "'," + (j / 4 + 1) + ");");
//
//                }
//
//            }
//            System.out.println("success!");
//        } catch (SQLException ex) {
//            System.err.println(ex);
//        }
//        
//
//    }
    public static void readExceltoDb(String gOrA,String excelName) throws Exception {
       
        ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
        ArrayList<String> unit = new ArrayList<String>();
        ArrayList<String> julienBarcode = new ArrayList<String>();
        ArrayList<String> testCode = new ArrayList<String>();
        

        String fileName = "C:\\Users\\Wei Wang\\Desktop\\work\\food sensitivity hamiltion clarity run\\"+excelName+".xlsx";
        FileInputStream inputStream = new FileInputStream(fileName);
        //1.讀取工作簿 
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        //2. 讀取工作表
        Sheet sheet = workbook.getSheet("All Results Ig" + gOrA);

        String pillarPlateId = sheet.getRow(1).getCell(0).getStringCellValue();
        Row row = sheet.getRow(1);
        for (int i = 0; i < 22; i++) {
            julienBarcode.add(row.getCell(i + 2).getStringCellValue());
        }

        for (int j = 0; j < 97; j++) {
            row = sheet.getRow(j + 2);
            testCode.add(row.getCell(1).getStringCellValue());

            for (int k = 0; k < 22; k++) {
                unit.add(Double.toString(row.getCell(k + 2).getNumericCellValue()));
            }
        }

        julienBarcode.add(pillarPlateId);// julienbarcode at allData.get(1).get(22)
        workbook.close();
        inputStream.close();

        allData.add(testCode);
        allData.add(julienBarcode);
        allData.add(unit);
        
        System.out.println(allData.get(1).get(22));
        System.out.println(fileName);
        System.out.println(pillarPlateId);
               
        
        //write to database
        try {
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
            Statement stmt = myConn.createStatement();
//insert unit
            for (int i = 0; i < 97; i++) {
                for (int j = 0; j < 22; j++) {
                    stmt.executeUpdate("insert into tsp_test_unit_data.foo" + gOrA + "_unit_data (test_name,julien_barcode,unit, pillar_plate_id,row,col) values('"
                            + allData.get(0).get(i) + "_unit','" + allData.get(1).get(j) + "','" + allData.get(2).get(i * 22 + j) + "','" + allData.get(1).get(22) + "','" + (char) (j % 4 + 65) + "'," + (j / 4 + 1) + ");");

                }

            }
//udpate the qc and lis

            CalTool cl = new CalTool();
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='"+allData.get(1).get(22)+"';");
            stmt.executeUpdate("insert into `tsp_test_qc_data`.`test_qc_data`(test_name,pillar_plate_id,cal_1,pos_ctrl_1,time) values ('FOO"+ Character.toUpperCase(gOrA.charAt(0))+"','"+allData.get(1).get(22)+
                    "','"+cl.getRandom(5, 15)+"','"+cl.getRandom(25, 35)+"',now());");
            
            System.out.println("successfully insert into DataBse!");
        } catch (SQLException ex) {
            System.err.println(ex);
        }

    }
}
