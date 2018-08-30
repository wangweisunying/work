
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UpdateTemplate5 {

    static void run(ArrayList<String> list, String well_plate_id) {
        writeTmp(list.get(0));
        createTmp(list.get(1), well_plate_id);
        System.out.println(well_plate_id );
        try {
            OpenLocalFile.useAWTDesktop(well_plate_id);
        } catch (IOException ex) {

        }

    }

    private static void createTmp(String pillarID, String well_plate_id) {
        createExcel(pillarID, "Run 2 Ig" + pillarID.charAt(3), getData("call Fetch24pillar('" + pillarID + "')"),
                well_plate_id);
    }

    private static void writeTmp(String pillarID) {
        updateExcel(pillarID, "Run 2 Ig" + pillarID.charAt(3), getData("call Fetch24pillar('" + pillarID + "')"));
    }

    private static void createExcel(String pillarPlateId, String sheetName, ArrayList<String> list,
            String well_plate_id) {

        try {
            

            // Workbook wb = new HSSFWorkbook(); xls
            FileInputStream fileIn = new FileInputStream("Y://food_tmp.xlsx");
            Workbook wb = new XSSFWorkbook(fileIn);
            Sheet sheet = wb.getSheet(sheetName);

            // Row row = sheet.createRow((short) 0);
            Row[] row = new Row[101];
            Cell[][] cell = new Cell[101][25];

            // set index to pillarPlateID
            row[0] = sheet.getRow(1);
            cell[0][0] = row[0].getCell(0);
            cell[0][0].setCellValue(pillarPlateId);

            // set the first row including string
            for (int k = 1; k < 25; k++) {
                cell[0][k] = row[0].getCell(k);
                cell[0][k].setCellValue(list.get(k));
            }
            for (int i = 1; i < 101; i++) {
                row[i] = sheet.getRow(i + 1);
                for (int j = 0; j < 25; j++) {
                    cell[i][j] = row[i].getCell(j);
                    cell[i][j].setCellValue(Double.parseDouble(list.get(25 * i + j)));
                }
            }

            FileOutputStream fileOut = new FileOutputStream(
                    "C:\\Users\\Wei Wang\\Desktop\\work\\food sensitivity hamiltion clarity run\\" + well_plate_id +  ".xlsx");
            wb.setForceFormulaRecalculation(true);

            wb.write(fileOut);
            fileOut.close();
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");

    }

    private static void updateExcel(String pillarPlateId, String sheetName, ArrayList<String> list) {
        try {
            

            // Workbook wb = new HSSFWorkbook(); xls
            FileInputStream fileIn = new FileInputStream("Y://food_tmp.xlsx");
            Workbook wb = new XSSFWorkbook(fileIn);
            Sheet sheet = wb.getSheet(sheetName);

            // Row row = sheet.createRow((short) 0);
            Row[] row = new Row[101];
            Cell[][] cell = new Cell[101][25];

            // set index to pillarPlateID
            row[0] = sheet.getRow(1);
            cell[0][0] = row[0].getCell(0);
            cell[0][0].setCellValue(pillarPlateId);

            // set the first row including string
            for (int k = 1; k < 25; k++) {
                cell[0][k] = row[0].getCell(k);
                cell[0][k].setCellValue(list.get(k));
            }
            for (int i = 1; i < 101; i++) {
                row[i] = sheet.getRow(i + 1);
                for (int j = 0; j < 25; j++) {
                    cell[i][j] = row[i].getCell(j);
                    cell[i][j].setCellValue(Double.parseDouble(list.get(25 * i + j)));
                }
            }

            FileOutputStream fileOut = new FileOutputStream("Y://food_tmp.xlsx");

            wb.setForceFormulaRecalculation(true);// 

            wb.write(fileOut);
            fileOut.close();
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");

    }

    public static ArrayList<String> getData(String query) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/vibrant_test_raw_data?useSSL=false", "TSPI3", "000028");
            // 2. create a statement
            // 3.execute sql query
            // query ="call
            // Fetch24wellpillar('FOOD201708191','FOOG80020070000046_20170826130614') ";
            // 4.process the result set
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                for (int i = 1; i < 26; i++) {
                    list.add(rs.getString(i));
                }

            }
        } catch (Exception exc) {
            System.err.println(exc);
        }

        // for(int y=0;y<arr.length;y++){
        // System.out.println(arr[y]);
        // }
        return list;

    }

    public static ArrayList<String> getData_pillarId(String query) {
        ArrayList<String> list = new ArrayList<String>();

        try {
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/vibrant_test_raw_data?useSSL=false", "TSPI3", "000028");
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException exc) {
            System.err.println(exc);
        }

        // for(int y=0;y<arr.length;y++){
        // System.out.println(arr[y]);
        // }
        return list;

    }
}
