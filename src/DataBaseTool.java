
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wei Wang
 */
public class DataBaseTool {
    
    
    public static void main(String args[]){
//        System.out.println("12,23".split(",")[1]);
//     getFoodDup("food201803271");   

//            insertZigData(ExcelTool.readZigExcel("zig"));
//    System.out.println(getDup(""));
        getZigDup();
    }
      
    public static ArrayList<String> serverInfo(String server) {
        ArrayList<String> s = new ArrayList<String>();
        switch (server) {
            case "V7":
                s.add("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false");
                s.add("TSPI3");
                s.add("000028");
                break;
            case "Linux":
                s.add("jdbc:mysql://192.168.10.153/vibrant_america_information?useSSL=false");
                s.add("wang");
                s.add("wang");
                break;
            case "VG":
                s.add("jdbc:mysql://192.168.10.187/lis_lab_info?useSSL=false");
                s.add("VG");
                s.add("vibrant@2015");
                break;
        }

        return s;
    }
    
 
    
    
    
     public static void checkDupZigData(ArrayList<ArrayList<String>> list_new,ArrayList<String> list_old) {
            
   
            /*
            combine list new and old find the result appear in old and delete in new  show them to the jtable, 
            add the updated result back to the end of the list_new than insert

            */
            ArrayList<ArrayList<String>> show = new ArrayList<>();
            System.out.println(list_new.size());
            for(int x = 0;x<list_new.size();x++){
                if(list_old.contains(list_new.get(x).get(0))){
                   show.add(list_new.get(x));
                }  
            }
//            list_new.removeAll(show);
//            System.out.println(show.size());
//            System.out.println(list_new.size());
//            System.out.println("show is "+show);
            //show in the Jtable
            Object [][] arr = new Object[show.size()][4]; 
            for(int i = 0;i<show.size();i++){
                arr[i][0] = show.get(i).get(0);
                arr[i][1] = show.get(i).get(1);
                arr[i][2] = list_old.get(list_old.indexOf(show.get(i).get(0))+1);
                arr[i][3] = Boolean.FALSE;
            } 

            Object[] headers = {"julien_barcode","current_unit","previous_unit","update"}; 
            Dup_table table = new Dup_table();
            table.init(arr,headers);
       
    } 
     
     
    
     
            public static void insertZigData(ArrayList<ArrayList<String>> list_new,ArrayList<String> list_ui) {
            for(int i=0;i<list_new.size();i++){
                if(list_ui.contains(list_new.get(i).get(0))){
                    list_new.get(i).set(1,list_ui.get(list_ui.indexOf(list_new.get(i).get(0))+1));
                }
            }
        
        
        
        try {
            Connection myConn = DriverManager.getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
            Statement stmt = myConn.createStatement();
 
            
            for(int i = 0 ;i < list_new.size();i++){
                stmt.executeUpdate("insert into `tsp_test_unit_data`.`test_unit_data` (`test_name`,`julien_barcode`,`unit`,`pillar_plate_id`,`row`,`col`,`time`) values ('ZIG_unit','"+list_new.get(i).get(0)+"','"+list_new.get(i).get(1)+"','"+list_new.get(i).get(2)+"','"+list_new.get(i).get(3)+"','"+list_new.get(i).get(4)+"',now());");
                System.out.println("insert into `tsp_test_unit_data`.`test_unit_data` (`test_name`,`julien_barcode`,`unit`,`pillar_plate_id`,`row`,`col`,`time`) values ('ZIG_unit','"+list_new.get(i).get(0)+"','"+list_new.get(i).get(1)+"','"+list_new.get(i).get(2)+"','"+list_new.get(i).get(3)+"','"+list_new.get(i).get(4)+"',now());");
            }
            System.out.println(list_new.get(0).get(2));
            stmt.executeUpdate("insert into tsp_test_qc_data.test_qc_data (`test_name`,`pillar_plate_id`,`cal_1`,`pos_ctrl_1`,`neg_ctrl_1`,`TIME`) values('zig_qc','"+list_new.get(0).get(2)+"','pass','pass','2.313600415668',now());");
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='"+list_new.get(0).get(2)+"';");
   
            System.out.println("data inserted!");
           
        } catch (SQLException exc) {
            System.err.println(exc);
        }
    } 
     
     
     
     

    public static ArrayList<ArrayList<String>> getZigData(ArrayList<String> serverInfo, String well_plate_id) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> name = new ArrayList<String>();
        name.add(well_plate_id);
        String query = "select pillar_plate_id,julien_barcode,`row`,`col`,`signal` from\n" +
"(select * from `vibrant_test_tracking`.well_info where well_plate_id = '"+well_plate_id+"' ) as a\n" +
"join (select * from vibrant_test_raw_data.zonulin where pillar_plate_id  = (select  pillar_plate_id from operation_data.instrument_ocupation where well_plate_id = '"+well_plate_id+"' order by start_time  desc limit 1 )) as b\n" +
"on concat(a.well_row,a.well_col) = concat(b.row,b.col);";
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection(serverInfo.get(0), serverInfo.get(1), serverInfo.get(2));
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                for(int i = 0 ;i<5;i++){
                    list.add(rs.getString(i+1));
                }
                

            }
            result.add(list);
            result.add(name);
            
        } catch (Exception exc) {
            System.err.println(exc);
        }

        // for(int y=0;y<arr.length;y++){
        // System.out.println(arr[y]);
        // }
        
        
        return result;
    }
        
        public static ArrayList<String> getZigDup() {
        ArrayList<String> res = new ArrayList<>();
        
        String query = "SELECT\n" +
"   substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',1) as julien_barcode,substring_index(group_concat(if(rwp.zig between -999999 and 0,null,rwp.zig)),',',1) as pre_unit\n" +
"FROM\n" +
"    vibrant_america_information.`patient_details` pd\n" +
"        JOIN\n" +
"    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n" +
"        JOIN\n" +
"    vibrant_america_information.`customers_of_patients` cop ON cop.`patient_id` = sd.`patient_id`\n" +
"        AND cop.`customer_id` = sd.`customer_id`\n" +
"        join\n" +
"          vibrant_america_information.`customer_details` cd on  cd.customer_id = sd.customer_id\n" +
"        AND cop.`customer_id` = sd.`customer_id`\n" +
"        join vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n" +
"        join `vibrant_america_test_result`.`result_wellness_panel1` rwp on rwp.sample_id = sd.sample_id\n" +
"WHERE\n" +
"   slt.Order_Wellness_Panel1 != 0 \n" +
"   group by PD.PATIENT_ID having count(*)>=2 and pre_unit is not null and julien_barcode>1801010000  order by substring(group_concat(sd.julien_barcode order by sd.julien_barcode desc),1,10) desc ;";
        System.out.println(query);
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.153/vibrant_america_information?useSSL=false","wang","wang");
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
               
                res.add(rs.getString("julien_barcode"));
                res.add(rs.getString("pre_unit"));
               
            }
            
        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
        }
        // for(int y=0;y<arr.length;y++){
        // System.out.println(arr[y]);
        // }
        
        System.out.println(res);
        for(String s : res){
            System.out.println(s);
        }
        return res;
        
    }
        
    public static void getFoodDup(String well_plate_id) {
        ArrayList<String> ju_new = new ArrayList<>();
        ArrayList<String> ju_old = new ArrayList<>();
        ArrayList<String>  res = new ArrayList<>();
        
        String query = "SELECT\n" +
"   substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2)\n" +
"FROM\n" +
"    vibrant_america_information.`patient_details` pd\n" +
"        JOIN\n" +
"    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n" +
"        JOIN\n" +
"    vibrant_america_information.`customers_of_patients` cop ON cop.`patient_id` = sd.`patient_id`\n" +
"        AND cop.`customer_id` = sd.`customer_id`\n" +
"        join\n" +
"          vibrant_america_information.`customer_details` cd on  cd.customer_id = sd.customer_id\n" +
"        AND cop.`customer_id` = sd.`customer_id`\n" +
"        join vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n" +
"        join `vibrant_america_test_result`.`result_wellness_panel20` rwp on rwp.sample_id = sd.sample_id \n" +
"        \n" +
"WHERE\n" +
"   slt.Order_Wellness_Panel20 != 0 and (rwp.yeast_iga >= 0 or rwp.yeast_iga = -1)\n" +
"   group by PD.PATIENT_ID having count(*)>=2 order by substring(group_concat(sd.julien_barcode order by sd.julien_barcode desc),1,10) desc ;\n" +
"   ";
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.153/vibrant_america_information?useSSL=false","wang","wang");
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {  
                ju_new.add(rs.getString(1).split(",")[0]);
                ju_old.add(rs.getString(1).split(",")[1]);
            }
            
        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
        }
        
                
        
        // food duplicate combine with current table
        String query_food = "select julien_barcode from `vibrant_test_tracking`.`well_info` where well_plate_id = '"+well_plate_id+"';";
        
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false","TSPI3","000028");
            Statement stmt = myConn.createStatement();
            ResultSet rs = stmt.executeQuery(query_food);
            while (rs.next()) {  
                if(ju_new.contains(rs.getString(1))){
                    res.add(rs.getString(1));
                    res.add(ju_old.get(ju_new.indexOf(rs.getString(1))));
                }
                
            }
            
        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
        }
        
        System.out.println(res);
        
        // find all the data in the foog and fooa
        ArrayList<String> show = new ArrayList();
        for (int i = 0; i < res.size() / 2; i++) {
            String query_g = " select concat(a.julien_barcode,\" vs \",b.julien_barcode) as julien_barcode, a.test_name,a.unit as `new` ,b.unit as `old`  from \n"
                    + "(select * from tsp_test_unit_data.foog_unit_data where julien_barcode = '" + res.get(2 * i) + "') as a \n"
                    + "join (select * from tsp_test_unit_data.foog_unit_data where julien_barcode = '" + res.get(2 * i + 1) + "') as b\n"
                    + " on a.`test_name` = b.test_name where (a.unit >=10 and b.unit<10) or (a.unit <10 and b.unit>=10); ";
            
            String query_a = "select concat(a.julien_barcode,\" vs \",b.julien_barcode) as julien_barcode, a.test_name,a.unit as `new` ,b.unit as `old`  from \n"
                    + "(select * from tsp_test_unit_data.fooa_unit_data where julien_barcode = '" + res.get(2 * i) + "') as a \n"
                    + "join (select * from tsp_test_unit_data.fooa_unit_data where julien_barcode = '" + res.get(2 * i + 1) + "') as b\n"
                    + " on a.`test_name` = b.test_name where (a.unit >=10 and b.unit<10) or (a.unit <10 and b.unit>=10);";

            try {
                // 1.get a connection to database
                Connection myConn = DriverManager
                        .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
                Statement stmt = myConn.createStatement();
                ResultSet rs = stmt.executeQuery(query_g);
                while (rs.next()) {
                    show.add(rs.getString(1));
                    show.add(rs.getString(2));
                    show.add(rs.getString(3));
                    show.add(rs.getString(4));
                }
                ResultSet rs1 = stmt.executeQuery(query_a);
                while(rs1.next()){
                    show.add(rs1.getString(1));
                    show.add(rs1.getString(2));
                    show.add(rs1.getString(3));
                    show.add(rs1.getString(4));
                }

        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
        }
        
        
        }

        System.out.println(show);
        Object[][] arr = new Object[show.size()/4][5];
        for (int i = 0; i < show.size()/4; i++) {
            arr[i][0] = show.get(4*i);
            arr[i][1] = show.get(4*i+1);
            arr[i][2] = show.get(4*i+2);
            arr[i][3] = show.get(4*i+3);
            arr[i][4] = Boolean.FALSE;
        }

        Object[] headers = {"julien_barcode","test_name","current_unit", "previous_unit", "update"};
        Dup_table_FS table = new Dup_table_FS();
        table.init(arr, headers);

    }



    static void update_FS_dup(ArrayList<String> modi) {
            try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false","TSPI3","000028");
            Statement stmt = myConn.createStatement();
            for(int i = 0 ;i<modi.size()/3;i++){
                switch(modi.get(3*i+1).substring(modi.get(3*i+1).length()-8,modi.get(3*i+1).length())){
                    case "IGG_unit":
                            stmt.executeUpdate("update tsp_test_unit_data.foog_unit_data set unit = '"+modi.get(3*i+2)+"' where julien_barcode = '"+modi.get(3*i)+"' and test_name = '"+modi.get(3*i+1)+"'; ");
                            System.out.println("update tsp_test_unit_data.foog_unit_data set unit = '"+modi.get(3*i+2)+"' where julien_barcode = '"+modi.get(3*i)+"' and test_name = '"+modi.get(3*i+1)+"'; ");
                            
                            
                        break;
                    case "IGA_unit":
                            stmt.executeUpdate("update tsp_test_unit_data.fooa_unit_data set unit = '"+modi.get(3*i+2)+"' where julien_barcode = '"+modi.get(3*i)+"' and test_name = '"+modi.get(3*i+1)+"'; ");
                            System.out.println("update tsp_test_unit_data.fooa_unit_data set unit = '"+modi.get(3*i+2)+"' where julien_barcode = '"+modi.get(3*i)+"' and test_name = '"+modi.get(3*i+1)+"'; ");
                        break;
                
                }
            
            }
            
            System.out.println("finish update!!");
            
        } catch (SQLException exc) {
            System.err.println(exc.getMessage());
        }
    }
    
    
    static void update_FS_fail(ArrayList<String> list,String well_id){
        String failed_slot = ArrayListTool.to_sql_in(list);
        if (failed_slot.isEmpty()) {
            System.out.println("nothing need to set -1");
        } else {
            try {
                // 1.get a connection to database
                Connection myConn = DriverManager
                        .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
                Statement stmt = myConn.createStatement();
                String query = "select pillar_plate_id from vibrant_test_tracking.pillar_plate_info where well_plate_id = '" + well_id + "';";
                ResultSet rs = stmt.executeQuery(query);
                ArrayList<String> pillar_id = new ArrayList<String>();
                while (rs.next()) {
                    pillar_id.add(rs.getString(1));
                }
                for (String s : pillar_id) {
                    if (s.substring(3, 4).equals("G")) {
                        stmt.executeUpdate("update tsp_test_unit_data.foog_unit_data set unit = -1 where concat(row,col) in (" + failed_slot + ") and pillar_plate_id = '" + s + "';");
                        System.out.println("update tsp_test_unit_data.foog_unit_data set unit = -1 where concat(row,col) in (" + failed_slot + ") and pillar_plate_id = '" + s + "';");
                    } else if (s.substring(3, 4).equals("A")) {
                        stmt.executeUpdate("update tsp_test_unit_data.fooa_unit_data set unit = -1 where concat(row,col) in (" + failed_slot + ") and pillar_plate_id = '" + s + "';");
                        System.out.println("update tsp_test_unit_data.fooa_unit_data set unit = -1 where concat(row,col) in (" + failed_slot + ") and pillar_plate_id = '" + s + "';");
                    }
                }

                System.out.println("finish set to -1!!");

            } catch (SQLException exc) {
                System.err.println(exc.getMessage());
            }
        }


    }
    
}
