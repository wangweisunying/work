
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
//description:
//take the duplication julienbarcode from linux compare to the exsist julienbarcode we run and then update all the new unit which
//is above 10 to 9 whilt old unit below 10. 
/**
 *
 * @author Wei Wang
 */
public class FoodDupCheck {
    public static void main(String args[]){
        modifyDup("food201802072");
    }

    public static void runProcedure(String s) {
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
            // 2. create a statement
            // 3.execute sql query
            // query ="call
            // Fetch24wellpillar('FOOD201708191','FOOG80020070000046_20170826130614') ";
            // 4.process the result set
            Statement stmt = myConn.createStatement();
            stmt.executeQuery(s);

        } catch (SQLException exc) {
            System.err.println(exc);
        }
    }

    public static ArrayList<String> getData(String query, int i) {
        ArrayList<String> list = new ArrayList<String>();
        String dbInfo = "jdbc:mysql://192.168.10.153/vibrant_america_information?useSSL=false";
        String userId = "wang";
        String passwd = "wang";
        switch (i) {
            case 1:
                dbInfo = "jdbc:mysql://192.168.10.121/vibrant_test_raw_data?useSSL=false";
                userId = "TSPI3";
                passwd = "000028";
                break;
//            case 2:
//                dbInfo = "jdbc:mysql://192.168.10.153/vibrant_america_information?useSSL=false";
//                userId = "wang";
//                passwd = "wang";
//                break;
        }
        try {
            // 1.get a connection to database
            Connection myConn = DriverManager
                    .getConnection(dbInfo, userId, passwd);
            // 2. create a statement
            // 3.execute sql query
            // query ="call
            // Fetch24wellpillar('FOOD201708191','FOOG80020070000046_20170826130614') ";
            // 4.process the result set
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

    public static void modifyDup(String wellPlateId) {
        
        ArrayList<String> lreal = getData("select julien_barcode from vibrant_test_tracking.well_info where well_plate_id = '"+wellPlateId+"';", 1);
        
        
        ArrayList<String> ldup = getData("SELECT \n"
                + "    SUBSTRING_INDEX(GROUP_CONCAT(sd.julien_barcode\n"
                + "                ORDER BY sd.julien_barcode DESC),\n"
                + "            ',',\n"
                + "            2)\n"
                + "FROM\n"
                + "    vibrant_america_information.`patient_details` pd\n"
                + "        JOIN\n"
                + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                + "        JOIN\n"
                + "    vibrant_america_information.`customers_of_patients` cop ON cop.`patient_id` = sd.`patient_id`\n"
                + "        AND cop.`customer_id` = sd.`customer_id`\n"
                + "        JOIN\n"
                + "    vibrant_america_information.`customer_details` cd ON cd.customer_id = sd.customer_id\n"
                + "        AND cop.`customer_id` = sd.`customer_id`\n"
                + "        JOIN\n"
                + "    vibrant_america_information.selected_test_list slt ON slt.sample_id = sd.sample_id\n"
                + "        JOIN\n"
                + "    `vibrant_america_test_result`.`result_wellness_panel20` rwp ON rwp.sample_id = sd.sample_id\n"
                + "WHERE\n"
                + "    slt.Order_Wellness_Panel20 != 0\n"
                + "        AND (rwp.yeast_iga >= 0\n"
                + "        OR rwp.yeast_iga = - 1)\n"
                + "GROUP BY CONCAT(CONCAT(pd.patient_firstname,\n"
                + "                ' ',\n"
                + "                pd.patient_lastname),\n"
                + "        pd.patient_id,\n"
                + "        pd.patient_birthdate)\n"
                + "HAVING COUNT(*) >= 2\n"
                + "ORDER BY SUBSTRING(GROUP_CONCAT(sd.julien_barcode\n"
                + "        ORDER BY sd.julien_barcode DESC),\n"
                + "    1,\n"
                + "    10) DESC;\n"
                + "    \n"
                + "    \n"
                + "   \n"
                + "   \n"
                + "   ", 0);
        
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < ldup.size(); i++) {
            if (lreal.contains(ldup.get(i).substring(0, 10))) {
                list.add(ldup.get(i));
            }
        }
        System.out.println("current well plate sample : "+lreal);
        System.out.println("duplicated sample : "+ldup);
        System.out.println("dup samples : "+list);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                runProcedure("call FoodDupCheck(" + list.get(i).substring(0, 10) + "," + list.get(i).substring(11) + ");");
                System.out.println("finshUpdating   " + list.get(i).substring(0, 10) + "  from  " + list.get(i).substring(11));
                
                
            }
        }

        System.out.println("Done!");

    }
}
