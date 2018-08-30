/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

/**
 *
 * @author Wei Wang
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ALPS {
    // MANUAL INPUT PART-------------------------

    String well_plate_id = "ALPS201808241";

//    ----------------------------------------------
    public static void main(String args[]) {
        ALPS alps = new ALPS();
        alps.run(1, 1);
    }

    public void run(double cf_G, double cf_A) {
        // init the cfG cfA
//        double cf_G = 1, cf_A = 1;

        //map to save the data
        HashMap<String, Unit> map = new HashMap();

        //init counter  for  igg iga  
        int sumG = 0, sumA = 0, ctG = 0, ctA = 0;
        double posRateG = -1, posRateA = -1;

        //container init
        HashSet<String> set = new HashSet(Arrays.asList("G12", "A1", "B1", "C1", "D1", "A12", "H1", "H12"));
        HashSet<String> faiedLoc = new HashSet();
        //container for dup
        HashMap<String, Double> julien_barcode_G = new HashMap();
        HashMap<String, Double> julien_barcode_A = new HashMap();

        ArrayList<DupUnit> list = new ArrayList();

        try {
//------------------------------------------------------- get dup ------------------------------------------------------------------------------------------------  
            Connection conDup = DriverManager.getConnection(DataBaseInfo.name_LX, DataBaseInfo.user_LX, DataBaseInfo.pw_LX);
            Statement stmtDup = conDup.createStatement();
            String sqlG = "select b.cur_julien_barcode as julien_barcode, rwp.ALPSIGG_IGM \n"
                    + "FROM\n"
                    + "    vibrant_america_information.`patient_details` pd\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                    + "        join `vibrant_america_test_result`.`result_wellness_panel2` rwp on rwp.sample_id = sd.sample_id\n"
                    + "	\n"
                    + "join (SELECT\n"
                    + "	substring(substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2) , 12 , 22)  as julien_barcode ,\n"
                    + "    substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',1) as cur_julien_barcode\n"
                    + "FROM\n"
                    + "	vibrant_america_information.`patient_details` pd\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`customers_of_patients` cop ON cop.`patient_id` = sd.`patient_id`\n"
                    + "        AND cop.`customer_id` = sd.`customer_id`\n"
                    + "        join\n"
                    + "          vibrant_america_information.`customer_details` cd on  cd.customer_id = sd.customer_id\n"
                    + "        AND cop.`customer_id` = sd.`customer_id`\n"
                    + "        join vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n"
                    + //"        join `vibrant_america_test_result`.`result_wellness_panel2` rwp on rwp.sample_id = sd.sample_id\n" +
                    "WHERE\n"
                    + "   slt.Order_Wellness_Panel1 != 0 \n"
                    + "   group by PD.PATIENT_ID having count(*)>=2 and julien_barcode > 1706050000) as b\n"
                    + "   on sd.julien_barcode = b.julien_barcode;";

            String sqlA = "select b.cur_julien_barcode as julien_barcode, rwp.ALPSIGA \n"
                    + "FROM\n"
                    + "    vibrant_america_information.`patient_details` pd\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                    + "        join `vibrant_america_test_result`.`result_wellness_panel2` rwp on rwp.sample_id = sd.sample_id\n"
                    + "	\n"
                    + "join (SELECT\n"
                    + "	substring(substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2) , 12 , 22)  as julien_barcode ,\n"
                    + "    substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',1) as cur_julien_barcode\n"
                    + "FROM\n"
                    + "	vibrant_america_information.`patient_details` pd\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n"
                    + "        JOIN\n"
                    + "    vibrant_america_information.`customers_of_patients` cop ON cop.`patient_id` = sd.`patient_id`\n"
                    + "        AND cop.`customer_id` = sd.`customer_id`\n"
                    + "        join\n"
                    + "          vibrant_america_information.`customer_details` cd on  cd.customer_id = sd.customer_id\n"
                    + "        AND cop.`customer_id` = sd.`customer_id`\n"
                    + "        join vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n"
                    + "        join `vibrant_america_test_result`.`result_wellness_panel2` rwp on rwp.sample_id = sd.sample_id\n"
                    + "WHERE\n"
                    + "   slt.Order_Wellness_Panel1 != 0 \n"
                    + "   group by PD.PATIENT_ID having count(*)>=2 and julien_barcode > 1706050000) as b\n"
                    + "   on sd.julien_barcode = b.julien_barcode;";

            ResultSet setG = stmtDup.executeQuery(sqlG);
            while (setG.next()) {
                julien_barcode_G.put(setG.getString("julien_barcode"), setG.getDouble("ALPSIGG_IGM"));
            }

            ResultSet setA = stmtDup.executeQuery(sqlA);
            while (setA.next()) {
                julien_barcode_A.put(setA.getString("julien_barcode"), setA.getDouble("ALPSIGA"));
            }

            conDup.close();

//------------------------------------------------------- get raw n transfer---------------------------------------------------------------------------------
            Connection con = DriverManager.getConnection(DataBaseInfo.name_V7, DataBaseInfo.user_V7, DataBaseInfo.pw_V7);
            Statement stmt = con.createStatement();
            String sql = "select c.julien_barcode , a.* from\n"
                    + "(select pillar_plate_id, `row`, col, `signal` from vibrant_test_raw_data.alps) as a  join \n"
                    + "(select pillar_plate_id from vibrant_test_tracking.pillar_plate_info where well_plate_id = '" + well_plate_id + "' and status ='finish') as b on a.pillar_plate_id = b.pillar_plate_id  left join\n"
                    + "(select well_row, well_col, julien_barcode from vibrant_test_tracking.well_info where well_plate_id = '" + well_plate_id + "') as c\n"
                    + "on concat(a.row, a.col) = concat(c.well_row, c.well_col) order by pillar_plate_id; ";
            System.out.println(sql);

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                String pillar_plate_id = rs.getString("pillar_plate_id");
                String signal_type = pillar_plate_id.substring(3, 4);
                String julien_barcode = rs.getString("julien_barcode");
                String row = rs.getString("row");
                int col = rs.getInt("col");
                String loc = row + col + signal_type;
                double rawSingal = rs.getDouble("signal");
                if (rawSingal == -1) {
                    faiedLoc.add(row + col);
                }

                switch (signal_type) {
                    case "G":

                        //calculate unit
                        double unitG;
                        if (set.contains(row + String.valueOf(col))) {
                            unitG = rawSingal;
                        } else {
                            unitG = 4.5 * (0.0015 * rawSingal - 2.0952) * cf_G;
                            if (unitG < 0) {
                                unitG = 0;
                            }
                        }
                        map.put(loc, new Unit("ALPSIGG_IGM_unit", julien_barcode, unitG, pillar_plate_id, row, col));

                        // add dup data
                        if (julien_barcode_G.containsKey(julien_barcode)) {
                            list.add(new DupUnit(julien_barcode, julien_barcode_G.get(julien_barcode), unitG, loc, "ALPSIGG_IGM_unit"));
                        }
                        // count 
                        if (!set.contains(row + String.valueOf(col))) {
                            sumG++;
                            if (unitG >= 280) {
                                ctG++;
                            }
                        }

                        break;
                    case "A":
                        //calculate unit
                        double unitA;
                        if (set.contains(row + String.valueOf(col))) {
                            unitA = rawSingal;
                        } else {
                            unitA = 1.1 * (0.0023 * rawSingal - 0.6633) * cf_A;
                            if (unitA < 0) {
                                unitA = 0;
                            }
                        }
                        map.put(loc, new Unit("ALPSIGA_unit", julien_barcode, unitA, pillar_plate_id, row, col));

                        // add dup data
                        if (julien_barcode_A.containsKey(julien_barcode)) {
                            list.add(new DupUnit(julien_barcode, julien_barcode_A.get(julien_barcode), unitA, loc, "ALPSIGA_unit"));
                        }
                        //count
                        if (!set.contains(row + String.valueOf(col))) {
                            sumA++;
                            if (unitA >= 30) {
                                ctA++;
                            }
                        }

                        break;
                }

            }

            //calculate the pos rate
            posRateG = (double) ctG / sumG;
            posRateA = (double) ctA / sumA;
            if (sumG == 0 || sumA == 0) {
                System.out.println("sumG = " + sumG + " & sumA = " + sumA + " some plate is not stitched yet Or it may have duplicate plate");
                return;
            }

//            for(DupUnit x : list){
//                System.out.println(x.julien_barcode + x.type + x.pre_unit +"  "+ x.cur_unit);
//            }
//            for (String x : map.keySet()) {
//                map.get(x).testPrint();
//            }
            con.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

//---------------------------------------------------------------------UI-----------------------------------------------------------------------
        //table init
        Object[] headers = {"testName", "julien_barcode", "Pre_Unit", "Cur_Unit", "update to pre_unit?", "location"};

        for (int i = 0; i < list.size(); i++) {
            DupUnit du = list.get(i);
            if (faiedLoc.contains(du.loc.substring(0, du.loc.length() - 1))) {
                list.remove(i);
            }
        }

        Object[][] content = new Object[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            DupUnit du = list.get(i);
            content[i][0] = du.testName;
            content[i][1] = du.julien_barcode;
            content[i][2] = du.pre_unit;
            content[i][3] = du.cur_unit;
            content[i][4] = Boolean.FALSE;
            content[i][5] = du.loc;

        }
        ArrayList<String> locList = new ArrayList();
        for (String loc : map.keySet()) {
            if (faiedLoc.contains(loc.substring(0, loc.length() - 1))) {
                locList.add(loc);
            }
        }
        for (String loc : locList) {
            map.remove(loc);
        }

        ALPSUI table = new ALPSUI();

        table.init(content, headers, String.valueOf(posRateG * 100) + "%", String.valueOf(posRateA * 100) + "%", cf_G, cf_A, map);

    }

    public void insertDB(HashMap<String, Double> dupMap, HashMap<String, Unit> map) {
        // update the duplication data

        for (String dupLoc : dupMap.keySet()) {
            if (map.keySet().contains(dupLoc)) {
                map.get(dupLoc).unit = dupMap.get(dupLoc);
            }
        }

        // insert to the DB
        try {
            Connection con = DriverManager.getConnection(DataBaseInfo.name_V7, DataBaseInfo.user_V7, DataBaseInfo.pw_V7);
            Statement stmt = con.createStatement();

            for (String loc : map.keySet()) {
                String sql = "INSERT INTO `tsp_test_unit_data`.`test_unit_data` (`test_name`, `julien_barcode`, `unit`, `pillar_plate_id`, `row`, `col`) values ('"
                        + map.get(loc).test_name + "','" + map.get(loc).julien_barcode + "'," + map.get(loc).unit + ",'" + map.get(loc).pillar_plate_id + "','" + map.get(loc).row + "',"
                        + map.get(loc).col + ") on duplicate key update `unit` = " + map.get(loc).unit + ";";
                stmt.executeUpdate(sql);
                System.out.println(sql);
            }
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='" + map.get("A1A").pillar_plate_id + "';");
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='" + map.get("A1G").pillar_plate_id + "';");
            stmt.executeUpdate("insert into `tsp_test_qc_data`.`test_qc_data`(test_name,pillar_plate_id,cal_1,cal_2,cal_3,cal_4,pos_ctrl_1,neg_ctrl_1,time) values ('ALPSIGA','" + map.get("A1A").pillar_plate_id + "','100','50','25','12.5','62.5','1.5',now());");
            stmt.executeUpdate("insert into `tsp_test_qc_data`.`test_qc_data`(test_name,pillar_plate_id,cal_1,cal_2,cal_3,cal_4,pos_ctrl_1,neg_ctrl_1,time) values ('ALPSIGG_IGM','" + map.get("A1G").pillar_plate_id + "','100','50','25','12.5','62.5','1.5',now());;");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}

class DupUnit {

    String julien_barcode;
    double pre_unit;
    double cur_unit;
    String loc;
    String testName;

    DupUnit(String julien_barcode, double pre_unit, double cur_unit, String loc, String testName) {
        this.julien_barcode = julien_barcode;
        this.pre_unit = pre_unit;
        this.cur_unit = cur_unit;
        this.loc = loc;
        this.testName = testName;
    }
}
