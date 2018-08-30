/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean; 
import org.apache.commons.math3.stat.descriptive.moment.Variance; 
/**
 *
 * @author Wei Wang
 */
class UnitData {

    public String julien_barcode, unit, pillar_plate_id, row;
    public int col;

    UnitData( String julien_barcode, String unit, String pillar_plate_id, String row, int col) {
        this.julien_barcode = julien_barcode;
        this.unit = unit;
        this.pillar_plate_id = pillar_plate_id;
        this.row = row;
        this.col = col;
    }

}
public class LectinZoomer {
    public HashMap<String , HashMap<String ,UnitData>> wholeUnit; 
    private String pillar_plate_id = "TST2847T42847N11";
    private HashMap<String , HashMap> wholeRaw;
    private HashSet<String> JulienSet;
    private HashMap<String , String> LocJu_map;
    private HashMap<String , String> JuLoc_map; 
    
    //init the testCode
    private String[] testCode = {
        "BARLEY_LECTIN_IGA",
        "BARLEY_LECTIN_IGG",
        "BELL_PEPPER_LECTIN_IGA",
        "BELL_PEPPER_LECTIN_IGG",
        "CHICKPEA_LECTIN_IGA",
        "CHICKPEA_LECTIN_IGG",
        "CORN_LECTIN_IGA",
        "CORN_LECTIN_IGG",
        "CUCUMBER_LECTIN_IGA",
        "CUCUMBER_LECTIN_IGG",
        "LENTIL_LECTIN_IGA",
        "LENTIL_LECTIN_IGG",
        "LIMA_BEAN_LECTIN_IGA",
        "LIMA_BEAN_LECTIN_IGG",
        "MUNG_LECTIN_IGA",
        "MUNG_LECTIN_IGG",
        "PEA_LECTIN_IGA",
        "PEA_LECTIN_IGG",
        "PEANUT_LECTIN_IGA",
        "PEANUT_LECTIN_IGG",
        "POTATO_LECTIN_IGA",
        "POTATO_LECTIN_IGG",
        "RICE_LECTIN_IGA",
        "RICE_LECTIN_IGG",
        "RYE_LECTIN_IGA",
        "RYE_LECTIN_IGG",
        "SOYBEAN_LECTIN_IGA",
        "SOYBEAN_LECTIN_IGG",
        "TOMATO_LECTIN_IGA",
        "TOMATO_LECTIN_IGG",
        "KIDNEY_BEAN_LECTIN_IGA",
        "KIDNEY_BEAN_LECTIN_IGG",
        "CORN_AQUAPORIN_IGA",
        "CORN_AQUAPORIN_IGG",
        "SOYBEAN_AQUAPORIN_IGA",
        "SOYBEAN_AQUAPORIN_IGG",
        "SPINACH_AQUAPORIN_IGA",
        "SPINACH_AQUAPORIN_IGG",
        "TOBACCO_AQUAPORIN_IGA",
        "TOBACCO_AQUAPORIN_IGG",
        "TOMATO_AQUAPORIN_IGA",
        "TOMATO_AQUAPORIN_IGG",
        "POTATO_AQUAPORIN_IGA",
        "POTATO_AQUAPORIN_IGG",
        "BELL_PEPPER_AQUAPORIN_IGA",
        "BELL_PEPPER_AQUAPORIN_IGG"
    };

    
    private class DataLengthMismatchException extends Exception {
        DataLengthMismatchException(String msg) {
            super(msg);
        }
    }


    private class LinearParameter{
        double a , b ;
        LinearParameter(double a , double b) {
            this.a = a;
            this.b = b;
        }
    }

    LectinZoomer() {
        wholeUnit = new HashMap();
        wholeRaw = new HashMap();
        JulienSet = new HashSet();
    }

    public static void main(String[] args) throws SQLException{

        LectinZoomer zz = new LectinZoomer();
        zz.run();
    }

    public void run() {
        try {
            ArrayList<String[]> tableList = readTableInfo();
            readRawLectin(tableList);
            calculateUnit(4);
            
            
            
            
            
//            for(String key : wholeUnit.keySet()){
//                HashMap<String ,UnitData > curMap = wholeUnit.get(key);
//                for(String test : curMap.keySet()){
//                    System.out.println(key + " "+test   + " " + curMap.get(test).julien_barcode + " " + curMap.get(test).unit +" " + curMap.get(test).pillar_plate_id +
//                            " " + curMap.get(test).row +" " + curMap.get(test).col);
//                }
//
//            }
            getDup();
            
         
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        catch(DataLengthMismatchException ex){
            System.out.println(ex.getMessage());
        }
        
    }
    public void reshowUI(float[] modiCf){
        
    
    }
    private void getDup() throws SQLException{
        DataBaseCon db = new LXDataBaseCon();
        String sql = "SELECT\n"
                + "  substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2) as julien_barcode,substring_index(group_concat(pd.patient_firstname,' ',pd.patient_lastname),',',2) as name\n"
                + "FROM\n"
                + "    vibrant_america_information.`patient_details` pd\n"
                + "       JOIN\n"
                + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                + "        JOIN\n"
                + "         vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n"
                + "join vibrant_america_test_result.result_lectin_aquaporin_panel1 rwp on rwp.sample_id = sd.sample_id\n"
                + "WHERE\n"
                + "   slt.Order_Wellness_Panel1 != 0  and rwp.BARLEY_LECTIN_IGG >= -2 group by pd.`patient_id` having count(*)>1 and julien_barcode > 1801010000 order by julien_barcode desc;";
        
        ResultSet rs = db.read(sql); // julienBarcode  + name
        Map<String ,String[]> DupMap = new HashMap(); // cur , pre ,
        StringBuilder sb = new StringBuilder();
 
        while(rs.next()){
            String combinedJulien = rs.getString(1);
            String curJu = combinedJulien.split(",")[0];
            String preJu = combinedJulien.split(",")[1];
            if(JulienSet.contains(curJu)){
                String Name = rs.getString(2);
                sb.append(combinedJulien.split(",")[1]);
                sb.append("','");
                DupMap.put( combinedJulien.split(",")[0], new String[]{preJu , Name});
            }
        }
        String dupListString = "";
        if(sb.length() != 0){
            dupListString = sb.substring(0, sb.length() - 3).toString();
        }
        System.out.println(dupListString);
  
        String sqlUnit = "select t1.julien_barcode , t2.* from \n"
                + "vibrant_america_information.`sample_data`  t1 join (SELECT \n"
                + "    a.sample_id,\n"
                + "    a.BARLEY_LECTIN_IGA,\n"
                + "    a.BARLEY_LECTIN_IGG,\n"
                + "    a.BELL_PEPPER_LECTIN_IGA,\n"
                + "    a.BELL_PEPPER_LECTIN_IGG,\n"
                + "    a.CHICKPEA_LECTIN_IGA,\n"
                + "    a.CHICKPEA_LECTIN_IGG,\n"
                + "    a.CORN_LECTIN_IGA,\n"
                + "    a.CORN_LECTIN_IGG,\n"
                + "    a.CUCUMBER_LECTIN_IGA,\n"
                + "    a.CUCUMBER_LECTIN_IGG,\n"
                + "    a.LENTIL_LECTIN_IGA,\n"
                + "    a.LENTIL_LECTIN_IGG,\n"
                + "    a.LIMA_BEAN_LECTIN_IGA,\n"
                + "    a.LIMA_BEAN_LECTIN_IGG,\n"
                + "    a.MUNG_LECTIN_IGA,\n"
                + "    a.MUNG_LECTIN_IGG,\n"
                + "    a.PEA_LECTIN_IGA,\n"
                + "    a.PEA_LECTIN_IGG,\n"
                + "    a.PEANUT_LECTIN_IGA,\n"
                + "    a.PEANUT_LECTIN_IGG,\n"
                + "    a.POTATO_LECTIN_IGA,\n"
                + "    a.POTATO_LECTIN_IGG,\n"
                + "    a.RICE_LECTIN_IGA,\n"
                + "    a.RICE_LECTIN_IGG,\n"
                + "    a.RYE_LECTIN_IGA,\n"
                + "    a.RYE_LECTIN_IGG,\n"
                + "    a.SOYBEAN_LECTIN_IGA,\n"
                + "    a.SOYBEAN_LECTIN_IGG,\n"
                + "    a.TOMATO_LECTIN_IGA,\n"
                + "    a.TOMATO_LECTIN_IGG,\n"
                + "    a.KIDNEY_BEAN_LECTIN_IGA,\n"
                + "    b.KIDNEY_BEAN_LECTIN_IGG,\n"
                + "    b.CORN_AQUAPORIN_IGA,\n"
                + "    b.CORN_AQUAPORIN_IGG,\n"
                + "    b.SOYBEAN_AQUAPORIN_IGA,\n"
                + "    b.SOYBEAN_AQUAPORIN_IGG,\n"
                + "    b.SPINACH_AQUAPORIN_IGA,\n"
                + "    b.SPINACH_AQUAPORIN_IGG,\n"
                + "    b.TOBACCO_AQUAPORIN_IGA,\n"
                + "    b.TOBACCO_AQUAPORIN_IGG,\n"
                + "    b.TOMATO_AQUAPORIN_IGA,\n"
                + "    b.TOMATO_AQUAPORIN_IGG,\n"
                + "    b.POTATO_AQUAPORIN_IGA,\n"
                + "    b.POTATO_AQUAPORIN_IGG,\n"
                + "    b.BELL_PEPPER_AQUAPORIN_IGA,\n"
                + "    b.BELL_PEPPER_AQUAPORIN_IGG\n"
                + "FROM\n"
                + "    vibrant_america_test_result.result_lectin_aquaporin_panel1 AS a\n"
                + "        JOIN\n"
                + "    vibrant_america_test_result.result_lectin_aquaporin_panel2 AS b ON a.sample_id = b.sample_id) t2\n"
                + "on t1.sample_id = t2.sample_id  where julien_barcode in ('" + dupListString + "');";
        
        Map<String ,List<String>> preUnit = new HashMap();
        ResultSet rsUnit = db.read(sqlUnit);
        while(rsUnit.next()){
            String julienBarcode = rsUnit.getString(1);
            List<String> tmp = new ArrayList();
            for(int i = 0 ; i < 46 ; i++){
                tmp.add(rsUnit.getString(i + 3));
            }
            preUnit.put(julienBarcode , tmp);
        }
        // init the dup data 
        ArrayList<Data> UItableList = new ArrayList();
        for(String curJun : DupMap.keySet()){
            String loc = JuLoc_map.get(curJun);
            String preJun = DupMap.get(curJun)[0];
            String name = DupMap.get(curJun)[1];
            
            
            for(int i = 0 ; i < preUnit.get(preJun).size() ; i++){       
               HashMap<String ,UnitData > curMap  = wholeUnit.get(loc);            
               String cur_unit  = curMap.get(testCode[i]).unit;
               String pre_unit = preUnit.get(preJun).get(i);
               //add filter before showing UI
               //add filter before showing UI end
               
               
               UItableList.add(new Data(testCode[i] , name , curJun , cur_unit ,preJun, pre_unit ));
            }
                 
        }
        LectinZoomer_UIController.dupData = UItableList;
        LectinZoomer_UIController.finalUnit = wholeUnit;
        LectinZoomer_UIController.juLocMap = JuLoc_map;
        LectinZoomer_UIController.testName = testCode;
        LectinZoomer_UIController.DupMap = DupMap;
        LectinZoomer_UIController.preUnit = preUnit;
        
        
        System.out.println(LectinZoomer_UIController.dupData);
        LectinZoomer_UI_Main.main(null);
        
       
         

    }
    private LinearParameter linearReg(double[] x , double[] y) throws DataLengthMismatchException{
        if(x.length != y.length){
            throw new DataLengthMismatchException("X[] length = " + x.length + " Y[] length = " + y.length);
        }
        Mean mean = new Mean();
        double xMean = mean.evaluate(x);
        double yMean = mean.evaluate(y);
        
        Covariance cov = new Covariance();
        double covXy = cov.covariance(x, y);
        
        Variance var = new Variance();
        double varX = var.evaluate(x);
        
        double a = covXy/varX;
        double b = yMean -  xMean* covXy/varX;
        System.out.println(a);
        System.out.println(b);
        return new LinearParameter(a , b);
    }
    

    private void calculateUnit(int testGroupCode) throws SQLException, DataLengthMismatchException {
        HashMap<String , LinearParameter> paraMap = new HashMap();
        DataBaseCon db = new V7DataBaseCon();
        // get all the well map
        JuLoc_map = new HashMap();
        LocJu_map = new HashMap();
        String sql_well = "select  * from vibrant_test_tracking.well_info where well_plate_id ='Lectin_0"+ pillar_plate_id.substring(pillar_plate_id.length() - 2) +"';";
        ResultSet rs_locJu = db.read(sql_well);
        while(rs_locJu.next()){
            String loc = rs_locJu.getString(2) + rs_locJu.getString(3);
            LocJu_map.put(loc , rs_locJu.getString(4));
            JuLoc_map.put(rs_locJu.getString(4) , loc);
            JulienSet.add(rs_locJu.getString(4));
        }

        // get all the parameter
        String sql = "select * from vibrant_test_raw_data.test_parameter where test_group_code = "+ testGroupCode +";";
        ResultSet rs = db.read(sql);

        while(rs.next()){
            // caculate all the parameter
            double min = rs.getFloat(3) , cut = rs.getFloat(4) , max = rs.getFloat(5) , min_y = rs.getFloat(6) , cut_y = rs.getFloat(7), max_y = rs.getFloat(8);
            double[] x = {min , cut , max};
            double[] y = {min_y , cut_y , max_y};
            paraMap.put(rs.getString(1), linearReg(x , y));
        }

        for (String location : wholeRaw.keySet()) {
            // filter the mismatch
            if(!LocJu_map.containsKey(location)){
                continue;
            }
           
            HashMap<String , UnitData> unitMap = new HashMap();            
            HashMap<String, ArrayList<Double>> rawMap = wholeRaw.get(location);
            //get the negative 
            double igg_neg_mean = Tools_Calculate.mean(rawMap.get("NEG_IGG"));
            double iga_neg_mean = Tools_Calculate.mean(rawMap.get("NEG_IGA"));
            String row = location.substring(0 , 1);
            int col = Integer.parseInt(location.substring(1));
            
            for (String testName : rawMap.keySet()) {
                if (testName.equals("NEG_IGG") || testName.equals("NEG_IGA")) {
                    continue;
                }
                double curTest_mean = Tools_Calculate.mean(rawMap.get(testName));
                double tmp_neg = Pattern.matches(".*IGG", testName) ? igg_neg_mean : iga_neg_mean;
                double tmp = 3000 * curTest_mean / tmp_neg;
                float unit = (float) (paraMap.get(testName).a * tmp + paraMap.get(testName).b);
                
                unitMap.put(testName, new UnitData(LocJu_map.get(location) , String.valueOf(unit), pillar_plate_id ,row , col ));
            }
            wholeUnit.put(location , unitMap);
        }
        db.close();
    }
    // return name and location
    private ArrayList<String[]> readTableInfo() throws SQLException { 
        ArrayList<String[]> list = new ArrayList();
        DataBaseCon db = new V7DataBaseCon();
        String sql_table = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'chip_analysis_result_new' and table_name like '"+ pillar_plate_id+"%';";
        
        ResultSet rs_table = db.read(sql_table);
        
        while(rs_table.next()){
            String table_name = rs_table.getString(1);
            String location = table_name.substring(table_name.length() - 2);
//            System.out.println(location);
            list.add(new String[]{table_name , location});
        }
        
        db.close();
        return list;
    }
    private void readRawLectin(ArrayList<String[]> tableinfo) throws SQLException {
        DataBaseCon db = new V7DataBaseCon();

        for (String[] tableInfo : tableinfo) {
    
            String sql = "select b.class , b.protein_name  , a.MeanIntensity as IGG  , a.MeanAlex647 as IGA  , b.info_id from chip_analysis_result_new."+ tableInfo[0] +" as a join \n"
                    + "protein_seperate_new.nebbiolo_lectin_4 as b\n"
                    + "on a.row = b.row and a.col = b.col;";
            ResultSet rs = db.read(sql);
            if(!rs.first()){
                System.out.println("table  " + tableInfo[0] + " + is Empty!");
                continue;
            }
            HashMap<String, ArrayList<Double>> rawMap = new HashMap();
            while (rs.next()) {
                String className = rs.getString(1).toLowerCase(), protein_name = rs.getString(2).toLowerCase(), info_id = rs.getString(5).toLowerCase();
                float igg = rs.getFloat(3), iga = rs.getFloat(4);
                if (igg <= -1 && iga <= -1) {
                    continue;
                }
//                NEG
                if (Pattern.matches(".*neg.*", className)) {
                    insertRawMap("NEG", igg, iga , rawMap);
                    continue;
                }
//                BARLEY_LECTIN_IGG
                if (Pattern.matches(".*barley.*lectin.*", className)) {
                    insertRawMap("BARLEY_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                BELL_PEPPER_LECTIN_IGG
                if (Pattern.matches(".*jacalin-related lectin.*", protein_name)) {
                    insertRawMap("BELL_PEPPER_LECTIN", igg, iga ,rawMap);
                    continue;
                }
//                CHICKPEA_LECTIN_IGG
                if (Pattern.matches(".*chickpea.*lectin.*", className)) {
                    insertRawMap("CHICKPEA_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                CORN_LECTIN_IGG
                if (Pattern.matches(".*corn.*lectin.*", className)) {
                    insertRawMap("CORN_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                CUCUMBER_LECTIN_IGG
                if (Pattern.matches(".*cucumber.*lectin.*", className)) {
                    insertRawMap("CUCUMBER_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                LENTIL_LECTIN_IGG
                if (Pattern.matches(".*lentil.*lectin.*", className)) {
                    insertRawMap("LENTIL_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                LIMA_BEAN_LECTIN_IGG
                if (Pattern.matches(".*lima bean.*lectin.*", className)) {
                    insertRawMap("LIMA_BEAN_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                MUNG_LECTIN_IGG
                if (Pattern.matches(".*mung.*lectin.*", className)) {
                    insertRawMap("MUNG_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                PEA_LECTIN_IGG
                if (Pattern.matches(".*pea lectin.*", className)) {
                    insertRawMap("PEA_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                PEANUT_LECTIN_IGG
                if (Pattern.matches(".*peanut.*lectin.*", className)) {
                    insertRawMap("PEANUT_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                POTATO_LECTIN_IGG
                if (Pattern.matches(".*potato.*lectin.*", className) && !Pattern.matches(".*,.*", info_id)) {
                    insertRawMap("POTATO_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                RICE_LECTIN_IGG
                if (Pattern.matches(".*rice.*lectin.*", className)) {
                    insertRawMap("RICE_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                RYE_LECTIN_IGG
                if (Pattern.matches(".*rye.*lectin.*", className)) {
                    insertRawMap("RYE_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                SOYBEAN_LECTIN_IGG
                if (Pattern.matches(".*soybean.*lectin.*", className)) {
                    insertRawMap("SOYBEAN_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                TOMATO_LECTIN_IGG
                if (Pattern.matches(".*tomato.*lectin.*", className)) {
                    insertRawMap("TOMATO_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                KIDNEY_BEAN_LECTIN_IGG
                if (Pattern.matches(".*bean phytohemagglutinin.*", className)) {
                    insertRawMap("KIDNEY_BEAN_LECTIN", igg, iga , rawMap);
                    continue;
                }
//                CORN_AQUAPORIN_IGG
                if (Pattern.matches(".*aqp4.*corn.*", className)) {
                    insertRawMap("CORN_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                SOYBEAN_AQUAPORIN_IGG
                if (Pattern.matches(".*aqp4.*soybean.*", className)) {
                    insertRawMap("SOYBEAN_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                SPINACH_AQUAPORIN_IGG
                if (Pattern.matches(".*aqp4.*spinach.*", className)) {
                    insertRawMap("SPINACH_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                TOBACCO_AQUAPORIN_IGG
                if (Pattern.matches(".*aqp4.*tobacco.*", className)) {
                    insertRawMap("TOBACCO_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                TOMATO_AQUAPORIN_IGG
                if (Pattern.matches(".*tomato.*aqp4.*", protein_name)) {
                    insertRawMap("TOMATO_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                POTATO_AQUAPORIN_IGG
                if (Pattern.matches(".*potato.*lectin.*", className) && Pattern.matches(".*,.*", info_id)) {
                    insertRawMap("POTATO_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
//                BELL_PEPPER_AQUAPORIN_IGG
                if (Pattern.matches(".*aqp4.*bell pepper.*", className) && !Pattern.matches(".*jacalin-related lectin.*", protein_name)) {
                    insertRawMap("BELL_PEPPER_AQUAPORIN", igg, iga , rawMap);
                    continue;
                }
            }
            wholeRaw.put(tableInfo[1].toUpperCase(), rawMap);
            System.out.println(rawMap);
        }
        db.close();
    }

    private void insertRawMap(String testCode, double igg, double iga , HashMap<String, ArrayList<Double>> rawMap) {
        String testIGG = testCode + "_IGG", testIGA = testCode + "_IGA";
        if (igg > -1) {
            if (rawMap.containsKey(testIGG)) {
                rawMap.get(testIGG).add(igg);
            } else {
                rawMap.put(testIGG, new ArrayList(Arrays.asList(igg)));
            }
        }
        if (iga > -1) {
            if (rawMap.containsKey(testIGA)) {
                rawMap.get(testIGA).add(iga);
            } else {
                rawMap.put(testIGA, new ArrayList(Arrays.asList(iga)));
            }
        }
    }
}
