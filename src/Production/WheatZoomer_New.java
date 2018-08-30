/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 *
 * @author Wei Wang
 */
class DBData{
        String plate_id;
        double[] unit_arr;
        DBData(String plate_id ,double[] unit_arr){
            this.plate_id = plate_id;
            this.unit_arr = unit_arr;
        }
}
class WZ_Dup_info{
        String name;
        String[] unit;
        WZ_Dup_info(String name , String[] unit){
            this.name = name;
            this.unit = unit;
        }
}
class AdjustData{
    double[] cf;
    HashMap<String ,DBData> dbData;
    AdjustData(double[] cf , HashMap<String ,DBData> dbData){
        this.cf = cf;
        this.dbData = dbData;
    }
}

class Parameter{
    double[] cf_thres = {0.5 , 1.5};
    double[] igg_range = {0.4 , 0.75};
    HashSet<Integer> exclude_testIndex = new HashSet(Arrays.asList(8 , 9, 10, 11 ,34 , 35 , 36 ,37 , 38 , 39));
    double iga_range = 0.15;
    String[] exclude_julienBarcode = {"B3" , "B4" , "B5"};
}
public class WheatZoomer_New {
    
    //init all the data container 
    HashMap<String , String> preCurMap = new HashMap();
    double[] threshold = {5220.006519,2572.002595,6817.233721,3014.819262,5298.600088,1205.377859,4603.368642,1555.674785,1695.9789,1158.17269,2351.31,1867.597769,16400.24975,2233.854757,8818.353883,2200.513274,29318.44304,4045.301222,4297,2055,1770.602142,1221.919594,3790,1038.962551,3553.000999,1411.398408,3653.077086,961.1828843,3417.430136,980.4219961,964.0553894,378.1653001,3859.520891,1560.299536,7872.327371,2913.166594,6071.090028,4902.578119,3198.168956,2551.722};  
    String[] test_name = {"AGIGG","AGIGA", "ABGIGG", "ABGIGA", "GGIGG", "GGIGA", "OGIGG",
         "OGIGA", "AZIGG", "AZIGA", "AAIGG", "AAIGA", "HMWIGG", "HMWIGA", "LMWIGG", "LMWIGA", "GIGG", "GIGA", "PRIGG", 
         "PRIGA", "SEIGG", "SEIGA", "FIGG", "FIGA", "APIGG", "APIGA", "GLOIGG", "GLOIGA", "PUIGG", "PUIGA", "SOIGG", 
         "SOIGA", "WGIGG", "WGIGA", "T3IGG", "T3IGA", "T6IGG", "T6IGA" ,"TGP_FUSION_IGG" , "TGP_FUSION_IGA"};
    
    public static void main(String[] args){
        
        WheatZoomer_New wz = new WheatZoomer_New();
        wz.run();
     
    }
    
    public void run(){
        // edit your pillarId here
        String pillarId = "WZMR80060001000014";
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //init container
        HashMap<String ,DBData> dbData = new HashMap();
        HashMap<String , double[]> finalData = new HashMap();
        //mapping the location -- > julienbarcode;
        HashMap<String , String > julienMap = new HashMap();
        HashSet<String> julienSet = new HashSet();
        ArrayList<String> tableList = new ArrayList();
        
        
        try{
            Connection con = DriverManager.getConnection(DataBaseInfo.name_V7,DataBaseInfo.user_V7,DataBaseInfo.pw_V7);
            Statement stmt = con.createStatement();
            
        //get all the table list( 96 table)
            String sql_table = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'chip_analysis_result_new' and table_name like '" + pillarId + "%'";
            ResultSet rs_table = stmt.executeQuery(sql_table);
            
            if (!rs_table.isBeforeFirst()) {
                System.out.println("Plate id does not exist !!!");
                return;
            }
            while(rs_table.next()){
                tableList.add(rs_table.getString("table_name"));
            }
            
            
        //get all teh julienbarcode 
            String sql_julienbarcode = "SELECT \n" +
"    CONCAT(well_row, well_col) as loc , julien_barcode\n" +
"FROM\n" +
"    vibrant_test_tracking.well_info\n" +
"WHERE\n" +
"    well_plate_id = (SELECT \n" +
"            well_plate_id\n" +
"        FROM\n" +
"            vibrant_test_tracking.pillar_plate_info\n" +
"        WHERE\n" +
"            pillar_plate_id = '"+ pillarId +"');";
           
            ResultSet rs_ju = stmt.executeQuery(sql_julienbarcode);
            while(rs_ju.next()){
                julienSet.add(rs_ju.getString("julien_barcode"));
                julienMap.put(rs_ju.getString("loc"), rs_ju.getString("julien_barcode"));
            }
             
        //take out all the data
            for(String table : tableList){
                String sql = "SELECT \n" +
"    MeanIntensity,MeanAlex647,protein_names, antigen_class ,antigen_major_type,gene_name,sequence\n" +
"FROM\n" +
"    `chip_analysis_result_new`.`"+ table +"` AS a\n" +
"        JOIN\n" +
"    `greatwall_sequence_sh`.`03_chip_celiac_mask` AS b ON a.row = b.row AND a.col = b.col\n" +
"ORDER BY sequence;";
                ResultSet rs = stmt.executeQuery(sql);
                finalData.put(table.substring(19).toUpperCase(),calculatePillar(rs)); 
            }
        //--------------------- get the dbData-----------------   no need to recalculate 
        for(String curloc : finalData.keySet()){
            dbData.put(julienMap.get(curloc) , new DBData(pillarId , finalData.get(curloc)));
        }
        //---------------------getDupData ready---------------- no need to recalculate
        HashMap<String , WZ_Dup_info> dupUnit = getWZDup(julienSet);
        
        

        
//---------------------------------------------------------UI recalculate part----------------------------------
        
        //-----------------------autoadjust-------------------------start--------------
        AdjustData adjustData = autoAdjust(dbData);
        
        //-----------------------autoadjust-------------------------end--------------
        
        reshowUI(adjustData.dbData , dupUnit , adjustData.cf , preCurMap);    

        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

    }
    public void reshowUI(HashMap<String , DBData> original ,HashMap<String , WZ_Dup_info> dupUnit ,double[] modi_cf , HashMap<String , String> pre_curMap){      
        // deep copy 
        HashMap<String , DBData> dbData = new HashMap();
        for(String key : original.keySet()){
            double[] arr = new double[original.get(key).unit_arr.length];
            arr = original.get(key).unit_arr.clone();   
            dbData.put(key, new DBData(original.get(key).plate_id ,arr));
        }
        

        //Apply the modi_cf to the final unit & ct the all negtive samples
        ArrayList<String> negtive = new ArrayList();
        
        for(String tmp : dbData.keySet()){
            int ct = 0;
            for(int i = 0 ; i < modi_cf.length ; i ++){
                if(dbData.get(tmp).unit_arr[i] < 0.9){
                    ct ++;
                }
                if(dbData.get(tmp).unit_arr[i] < 0){
                    continue;
                }
                dbData.get(tmp).unit_arr[i] *= modi_cf[i];
                
            }
            if(ct == modi_cf.length){
                negtive.add(tmp);
            }
        }

        //----------------calculate Posistive rate-----------------------
        String[] posRateArr = getPostitiveRate(dbData);
        
        
        //combine all the dup info
        ArrayList<ArrayList<Object>> uiList = new ArrayList();
        for(String preJu : dupUnit.keySet() ){
            for(int i = 0 ; i < 40 ; i ++){
                String curJu = pre_curMap.get(preJu);
                //calcal date difference
                int year_dif = Integer.parseInt(curJu.substring(0, 2)) - Integer.parseInt(preJu.substring(0, 2));
                int month_dif = Integer.parseInt(curJu.substring(2, 4)) - Integer.parseInt(preJu.substring(2, 4));
                int day_dif = Integer.parseInt(curJu.substring(4, 6)) - Integer.parseInt(preJu.substring(4, 6));
                int total_dif = year_dif * 365 +  month_dif * 30 + day_dif;
                
                //generate ui 
                double preUnit = Double.parseDouble(dupUnit.get(preJu).unit[i]);
                double curUnit = dbData.get(curJu).unit_arr[i];
                if(i == 30 || i == 31){ // exclude SOIGG SOIGA
                    continue;
                }
     
                if( preUnit >= 0 && preUnit < 0.9 && curUnit >= 0.9 ||
                        ( preUnit >= 0.9 && curUnit < 0.9 && total_dif <= 30)){
                    ArrayList<Object> curline = new ArrayList();
                    curline.add(curJu + "-vs-" + preJu); //cur julienbarcode
                    curline.add(dupUnit.get(preJu).name);//name
                    curline.add(i +"-"+ test_name[i]);//test
                    curline.add(dupUnit.get(preJu).unit[i]); //pre unit
                    curline.add(String.valueOf(dbData.get(curJu).unit_arr[i])); // cur  
                    curline.add(Boolean.FALSE);
                    uiList.add(curline);
                }  
            }
        }
           
        //UI ----------------------------init--------------------
        
        
            
        Object[][] content = new Object[uiList.size()][6];
        for(int i = 0 ; i < uiList.size() ; i ++ ){
            for(int j = 0 ; j < 6 ; j++){
                content[i][j] = uiList.get(i).get(j);
            }
        }
        Object[] headers = {"julien_barcode","name" , "test" , "prev" , "cur" ,"update to pre_unit?"};
        WheatZoomer_UI ui = new WheatZoomer_UI();
        System.out.println(dbData);
        ui.init(content, headers, dbData , posRateArr , modi_cf ,dupUnit , pre_curMap , original ,negtive);
    
    }
    
    public void insertDB(HashMap<String,Double>  modiMap , HashMap<String ,DBData> dbData , String[] qcArr){
        HashMap<String , DBData> map = new HashMap();
        for(String key : dbData.keySet()){
            double[] arr = new double[dbData.get(key).unit_arr.length];
            arr = dbData.get(key).unit_arr.clone();   
            map.put(key, new DBData(dbData.get(key).plate_id ,arr));
        }
        
        
        
        //modify map first
        for(String tmp : modiMap.keySet()){
            String key = tmp.substring(0,10);
            int index = Integer.parseInt(tmp.substring(10));
            map.get(key).unit_arr[index] = modiMap.get(tmp);   
        }
        // exclude B3 B4 B5
//        HashMap<String , DBData > ctrlMap = new HashMap();
        ArrayList<String> list = new ArrayList();
        Parameter para = new Parameter();
        for(String julienBarcode : map.keySet()){
            if(julienBarcode == null){
                continue;
            }
            for(String ex : para.exclude_julienBarcode){
                if(julienBarcode.startsWith(ex)){
//                    ctrlMap.put(ex, map.get(julienBarcode));
                    list.add(julienBarcode);
                }
            }
        }
        for(String tmp : list){
            map.remove(tmp);
        }
//        double b4_cali = ctrlMap.get(para.exclude_julienBarcode[1]).unit_arr[39];
//        double b3_pos = ctrlMap.get(para.exclude_julienBarcode[0]).unit_arr[39];
//        double b5_neg = ctrlMap.get(para.exclude_julienBarcode[2]).unit_arr[39];
        double b4_cali = Double.parseDouble(qcArr[1]);
        double b3_pos = Double.parseDouble(qcArr[0]);
        double b5_neg = Double.parseDouble(qcArr[2]);
        
        
        try{
            Connection con = DriverManager.getConnection(DataBaseInfo.name_V7,DataBaseInfo.user_V7,DataBaseInfo.pw_V7);
            Statement stmt = con.createStatement();
            
            
            for(String julien_barcode : map.keySet()){
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                
//                sb.append("insert into vibrant_test_raw_data.wheatzoomer values ('"+ julien_barcode +"','"+ map.get(julien_barcode).plate_id +"','PASS','PASS',");
//                sb2.append(" on duplicate key update ");
                String pass = "PASS";
                for(int i = 0 ; i < map.get(julien_barcode).unit_arr.length ; i++){
                    if(map.get(julien_barcode).unit_arr[i] < 0){
                        pass = "FAIL";
                    }
                    sb.append("'" + map.get(julien_barcode).unit_arr[i] + "',");
                    sb2.append(test_name[i] + " = '" + map.get(julien_barcode).unit_arr[i] + "',");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                sb2.deleteCharAt(sb2.length() - 1);
                sb2.append(";");
                sb.insert(0, "insert into vibrant_test_raw_data.wheatzoomer values ('"+ julien_barcode +"','"+ map.get(julien_barcode).plate_id +"','"+ pass +"','"+ pass +"',");
                sb2.insert(0, " on duplicate key update ");
                
                String sql = sb.toString() + sb2.toString();
                System.out.println(sql);
                stmt.executeUpdate(sql);
            }
            Iterator it = map.keySet().iterator();
            String pillarId = map.get(it.next()).plate_id;
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='"+ pillarId +"';");
            stmt.executeUpdate("insert into `tsp_test_qc_data`.`test_qc_data` (test_name,pillar_plate_id,cal_1,pos_ctrl_1,neg_ctrl_1,time) \n" +
"values ('wheatzoomer','" + pillarId + "','"+ b4_cali +"','"+ b3_pos +"','"+ b5_neg +"',now()) on duplicate key update cal_1 = '"+ b4_cali +"' , pos_ctrl_1 = '"+ b3_pos +"' , neg_ctrl_1= '"+ b5_neg +"' ;");
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
    
    }
    
    private HashMap<String , WZ_Dup_info> getWZDup(HashSet<String> julienSet){
        //init the dupjulien 
        ArrayList<String> dupList = new ArrayList();
        HashMap<String , WZ_Dup_info> preUnit = new HashMap(); // 
        
        try{
            Connection con = DriverManager.getConnection(DataBaseInfo.name_LX,DataBaseInfo.user_LX,DataBaseInfo.pw_LX);
            Statement stmt = con.createStatement();
            String sql = "SELECT\n" +
"  substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2) as julien_barcode,substring_index(group_concat(pd.patient_firstname,' ',pd.patient_lastname),',',2) as name\n" +
"FROM\n" +
"    vibrant_america_information.`patient_details` pd\n" +
"       JOIN\n" +
"    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n" +
"        JOIN\n" +
"         vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n" +
"join `vibrant_america_test_result`.`result_wellness_panel1` rwp on rwp.sample_id = sd.sample_id\n" +
"WHERE\n" +
"   slt.Order_Wellness_Panel1 != 0  and rwp.AZIGG >= -2 group by pd.`patient_id` having count(*)>1 and julien_barcode > 1801010000 order by julien_barcode desc;";
      
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String cur = rs.getString("julien_barcode").substring(0,10); 
                String pre = rs.getString("julien_barcode").substring(11);    
                if(julienSet.contains(cur)){
                    dupList.add(pre);
                    preCurMap.put(pre , cur);
                    preUnit.put(pre, new WZ_Dup_info(rs.getString("name"), new String[40]));
                }
            }
   
            String tmpQuery = "";
            for(String cur : dupList){
                tmpQuery += "'" + cur + "',";
            }
            String sqlDup = "select t1.julien_barcode,t2.* from vibrant_america_information.`sample_data` t1 join\n" +
"   (select p1.sample_id,AGIGG,AGIGA, ABGIGG, ABGIGA, GGIGG, GGIGA, OGIGG,\n" +
"         OGIGA, AZIGG, AZIGA, AAIGG, AAIGA, HMWIGG, HMWIGA, LMWIGG, LMWIGA, GIGG, GIGA, PRIGG, \n" +
"         PRIGA, SEIGG, SEIGA, FIGG, FIGA, APIGG, APIGA, GLOIGG, GLOIGA, PUIGG, PUIGA, SOIGG, \n" +
"         SOIGA, WGIGG, WGIGA, T3IGG, T3IGA, T6IGG, T6IGA ,TGP_FUSION_IGG , TGP_FUSION_IGA\n" +
"		from `vibrant_america_test_result`.`result_wellness_panel1` p1 join `vibrant_america_test_result`.`result_wellness_panel2` p2\n" +
"on p1.sample_id = p2.sample_id) t2\n" +
"	on t1.sample_id = t2.sample_id where julien_barcode in ("+ tmpQuery.substring(0 , tmpQuery.length() - 1) +");";       
            ResultSet rsDup = stmt.executeQuery(sqlDup);
            
            while(rsDup.next()){
                String pre = rsDup.getString("julien_barcode");
                for(int i = 0 ; i < 40 ; i ++){
                    preUnit.get(pre).unit[i] = rsDup.getString(i + 3);          
                }
            }           
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return preUnit;
    }
    
    private AdjustData autoAdjust(HashMap<String , DBData> dbData){
        //cf init 
        double[] cf = new double[40];
        for(int i = 0 ; i < test_name.length ; i ++){
            cf[i] = 1.0;
        }
        // posRate init 
        int total = dbData.size();
        int[] pos_arr = new int[test_name.length];
        //apply cf to the dbData 
        for(String key : dbData.keySet()){
            for(int i = 0 ; i < test_name.length ; i++){
                dbData.get(key).unit_arr[i] *= cf[i] ;
                if(dbData.get(key).unit_arr[i] >= 0.9){
                    pos_arr[i]++;
                }
            }
        }
        
        //deep copy the dbData
        HashMap<String , DBData> original = new HashMap();
        for(String key : dbData.keySet()){
            double[] arr = new double[dbData.get(key).unit_arr.length];
            arr = dbData.get(key).unit_arr.clone();   
            original.put(key, new DBData(dbData.get(key).plate_id ,arr));
        }
        
        //take those out of range index and adjust 
        Parameter para = new Parameter();
        for(int i = 0 ; i < test_name.length ; i ++){
            if(para.exclude_testIndex.contains(i)){
                continue;
            }
            double posRate =  pos_arr[i]/(double)total;
            switch( i % 2){
                case 0 : //igg
                    if( posRate > para.igg_range[1]){
                        while(posRate > para.igg_range[1] && cf[i] > para.cf_thres[0]){
                            int ctPos = 0;
                            cf[i] -= 0.05;
                            for(String key : dbData.keySet()){
                                dbData.get(key).unit_arr[i] = cf[i] * original.get(key).unit_arr[i];
                                if(dbData.get(key).unit_arr[i] > 0.9){
                                    ctPos ++;
                                }
                            }
                            posRate = ctPos /(double) total;
                        }
                    }
                    else if(posRate < para.igg_range[0]){
                        while(posRate < para.igg_range[0] && cf[i] < para.cf_thres[1]){
                            int ctPos = 0;
                            cf[i] += 0.05;
                            for(String key : dbData.keySet()){
                                dbData.get(key).unit_arr[i] = cf[i] * original.get(key).unit_arr[i];
                                if(dbData.get(key).unit_arr[i] > 0.9){
                                    ctPos ++;
                                }
                            }
                            posRate = ctPos /(double)total;
                        }
                    }
                    break;
                    
                case 1 : //iga
                    if(posRate > para.iga_range){
                        while(posRate > para.iga_range && cf[i] > para.cf_thres[0]){
                            int ctPos = 0;
                            cf[i] -= 0.05;
                            for(String key : dbData.keySet()){
                                dbData.get(key).unit_arr[i] = cf[i] * original.get(key).unit_arr[i];
                                if(dbData.get(key).unit_arr[i] > 0.9){
                                    ctPos ++;
                                }
                            }
                            posRate = ctPos /(double)total;
                        }
                    }      
                    break;
            }
        }
        System.out.println(Arrays.toString(cf));
        return new AdjustData(cf ,original);
    }
    
    public String[] getPostitiveRate(HashMap<String , DBData> dbData){
        String[] res = new String[40];
        int total = dbData.size();
        int[] pos_arr = new int[40];
        for(String curLoc : dbData.keySet()){
            for(int i = 0 ; i < 40 ; i ++ ){
               if(dbData.get(curLoc).unit_arr[i] >= 0.9){
                   pos_arr[i]++;
               }
            }
        }
        for(int i = 0 ; i < 40 ; i ++){
            res[i] = String.format("%.2f",(float)pos_arr[i]/total);
        }   
        return res;
    }
    
    private double[] calculatePillar(ResultSet rs) throws SQLException{
        //init all the calculater
        
        ArrayList<HashMap<String , ArrayList<Double>>> res = new ArrayList();
        
        for(int i = 0 ; i < 40 ; i ++){
            res.add(new HashMap<String , ArrayList<Double>>());
        }
        
        
        HashSet<String> TGP_FUSION_IGG_Set = new HashSet(Arrays.asList("EPQQPEQSFPEQ******","QPQQPFPEPEQPFPWQP*","QPQEPFPQPEQPFPWQE*","QQQPFPEQPFPQ******","QPQEPFPQPEQPFPWQP*","QQPPFSEEQEQP******","EQPFPEQPEQPF******","QPQEPFPEPEQPFPWQE*","QQPPFSEQQESP******","YSPYQPEQPFPQ******","EPQQPEESFPEQ******","QPQEPFPEPEQPFPWQP*","EPEQPQQSFPEQ******","EPEQPEESFPEQ******","EPEQPFPQPEQPFPWQE*","QPQEPFPQPEEPFPWQP*","QPQEPFPQPEEPFPWQE*","EPEQPFPQPEQPFPWQP*","FPQEQPEQPFPQ******","QEQPFPQPEQPF******","QQQPFPEQQSYP******"));
        HashSet<String> TGP_FUSION_IGA_Set = new HashSet(Arrays.asList("QPEQPEQSFPEQ******","QPQEPEQSFPQE******","QPQRPEQQFPQQPQQPFP","KVEALPEQVAPE******","LVQEQQFPGQQQPFPPQQ","SQPEQEFPQPQQ******","EPQQPEESFPEE******","QQFPRPQQSPEQQQFPQQ","QQVLPEQPPFSQ******","PQTQQPEQPFPQ******","LQLQPFPQPELPYPQPELPYPQPELPYPQPQPFFLQPEQPFPEQPQQPYPQEPQQPFPQ","QEQQFPGQQQPFPPQQPY","QPEQPEQSFPQE******","QPEQPEQSFPQQ******","QPQRPEQQFPQQPQQIIP","QQPFPQQPPQQPEQIISE","PNPLQPEQPFPL******","QLQRPEQQFPQQPQQIIP","RAIKEGQPEQPF******","HHTADLQPEQPF******","PEQPFPEQPQQP******","SEQPFPQTEQPQ******","VHFTMPEQPFRT******"));        
        
        
        while(rs.next()){
            String protein_names = rs.getString("protein_names").toLowerCase();
            String antigen_class = rs.getString("antigen_class").toLowerCase();
            String antigen_major_type = rs.getString("antigen_major_type").toLowerCase();
            String gene_name = rs.getString("gene_name").toLowerCase();
            String sequence = rs.getString("sequence");
            double igg = rs.getDouble("MeanIntensity");
            double iga = rs.getDouble("MeanAlex647");
            
            //AGIIGG IGA
            if(protein_names.contains("alpha gliadin") && antigen_class.contains("gluten sensitivity")){
                if(!res.get(0).containsKey(sequence)){
                    res.get(0).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(1).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(0).get(sequence).add(igg);
                    res.get(1).get(sequence).add(iga);
                }  
            }
            
            //ABGIGG IGA
            if(protein_names.contains("alpha/beta gliadin (fragment)") && antigen_class.contains("gluten sensitivity")){
                if(!res.get(2).containsKey(sequence)){
                    res.get(2).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(3).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(2).get(sequence).add(igg);
                    res.get(3).get(sequence).add(iga);
                }  
            }
            
            //GGIGG IGA
            if(protein_names.contains("gamma gliadin") && antigen_class.contains("gluten sensitivity")){
                if(!res.get(4).containsKey(sequence)){
                    res.get(4).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(5).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(4).get(sequence).add(igg);
                    res.get(5).get(sequence).add(iga);
                }  
            }
            
            //OGIGG IGA
            if(protein_names.contains("omega gliadin") && antigen_class.contains("gluten sensitivity")){
                if(!res.get(6).containsKey(sequence)){
                    res.get(6).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(7).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(6).get(sequence).add(igg);
                    res.get(7).get(sequence).add(iga);
                }  
            }
            
            //AZIGG IGA
            if(protein_names.contains("zonulin") && antigen_class.contains("gluten sensitivity")){
                if(!res.get(8).containsKey(sequence)){
                    res.get(8).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(9).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(8).get(sequence).add(igg);
                    res.get(9).get(sequence).add(iga);
                }  
            }
            
            //AAIGG IGA
            if(protein_names.contains("actin") && antigen_major_type.contains("gluten sensitivity")){
                if(!res.get(10).containsKey(sequence)){
                    res.get(10).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(11).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(10).get(sequence).add(igg);
                    res.get(11).get(sequence).add(iga);
                }  
            }
            
            //HMWIGG IGA
            if(protein_names.contains("hmw glutenin")){
                if(!res.get(12).containsKey(sequence)){
                    res.get(12).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(13).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(12).get(sequence).add(igg);
                    res.get(13).get(sequence).add(iga);
                }  
            }
            
            //LMWIGG IGA
            if(protein_names.contains("lmw glutenin")){
                if(!res.get(14).containsKey(sequence)){
                    res.get(14).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(15).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(14).get(sequence).add(igg);
                    res.get(15).get(sequence).add(iga);
                }  
            }
            
            //GIGG IGA
            if(protein_names.contains("gliadorphin")){
                if(!res.get(16).containsKey(sequence)){
                    res.get(16).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(17).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(16).get(sequence).add(igg);
                    res.get(17).get(sequence).add(iga);
                }  
            }
            
            //PRIGG IGA
            if(protein_names.contains("prodynorphin")){
                if(!res.get(18).containsKey(sequence)){
                    res.get(18).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(19).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(18).get(sequence).add(igg);
                    res.get(19).get(sequence).add(iga);
                }  
            }
            
            //SEIGG IGA
            if(protein_names.contains("serpin")){
                if(!res.get(20).containsKey(sequence)){
                    res.get(20).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(21).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(20).get(sequence).add(igg);
                    res.get(21).get(sequence).add(iga);
                }  
            }
            
            //FIGG IGA
            if(protein_names.contains("farinin")){
                if(!res.get(22).containsKey(sequence)){
                    res.get(22).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(23).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(22).get(sequence).add(igg);
                    res.get(23).get(sequence).add(iga);
                }  
            }
            
            //APIGG IGA
            if(protein_names.contains("amylase")){
                if(!res.get(24).containsKey(sequence)){
                    res.get(24).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(25).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(24).get(sequence).add(igg);
                    res.get(25).get(sequence).add(iga);
                }  
            }
            
            //GLOIGG IGA
            if(protein_names.contains("globulin")){
                if(!res.get(26).containsKey(sequence)){
                    res.get(26).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(27).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(26).get(sequence).add(igg);
                    res.get(27).get(sequence).add(iga);
                }  
            }
            
            //PUIGG IGA
            if(protein_names.contains("peroxiredoxin")){
                if(!res.get(28).containsKey(sequence)){
                    res.get(28).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(29).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(28).get(sequence).add(igg);
                    res.get(29).get(sequence).add(iga);
                }  
            }
            
            //SOIGG IGA
            if(protein_names.contains("somatostatin")){
                if(!res.get(30).containsKey(sequence)){
                    res.get(30).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(31).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(30).get(sequence).add(igg);
                    res.get(31).get(sequence).add(iga);
                }  
            }
            
            //WGIGG IGA
            if(protein_names.contains("agglut")){
                if(!res.get(32).containsKey(sequence)){
                    res.get(32).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(33).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(32).get(sequence).add(igg);
                    res.get(33).get(sequence).add(iga);
                }  
            }
            
            //T3IGG IGA
            if(gene_name.contains("tgm3")){
                if(!res.get(34).containsKey(sequence)){
                    res.get(34).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(35).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(34).get(sequence).add(igg);
                    res.get(35).get(sequence).add(iga);
                }  
            }
            
            //T6IGG IGA
            if(gene_name.contains("tgm6")){
                if(!res.get(36).containsKey(sequence)){
                    res.get(36).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                    res.get(37).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(36).get(sequence).add(igg);
                    res.get(37).get(sequence).add(iga);
                }  
            }
            
            //TGP_FUSION_IGG
            if(TGP_FUSION_IGG_Set.contains(sequence)){
                if(!res.get(38).containsKey(sequence)){
                    res.get(38).put(sequence, new ArrayList<Double>(Arrays.asList(igg)));
                }
                else{
                    res.get(38).get(sequence).add(igg);
                }  
            }
            
            //TGP_FUSION_IGA
            if(TGP_FUSION_IGA_Set.contains(sequence)){
                if(!res.get(39).containsKey(sequence)){
                    res.get(39).put(sequence, new ArrayList<Double>(Arrays.asList(iga)));
                }
                else{
                    res.get(39).get(sequence).add(iga);
                }  
            }             
        }
        
        //calculate all the Median save to the unit;
        double[] data = new double[40];
        for(int i = 0 ; i < 40 ; i ++ ){
            // in case some none result
            if(res.get(i).isEmpty()){
                data[i] = (double)-1;
                continue;
            }      
            ArrayList<Double> cur = new ArrayList();
            for(String squ : res.get(i).keySet()){
                cur.add(Tools_Calculate.findMedian(res.get(i).get(squ)));  //getMeidan 
            }
          
            data[i] = Tools_Calculate.mean(cur)/threshold[i]; // getmean /threshold
        }
        System.out.println(Arrays.toString(data));
        return data;
    }
    
    
}
