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
import java.util.HashMap;


/**
 *
 * @author Vibrant_5
 */
public class WheatZoomer {

    // Class WZTest contains test information

    private class WZTest {

        String test_code;
        String test_id;
        String test_result;
        String test_error;

    }

    // Class WZ contans sample linked with test information

    private class WZ {

        String sample_id;
        ArrayList<WZTest> wheat_zoomer;
        
    }
    
    
    
    public static void main(String[] args){
        WheatZoomer wz = new WheatZoomer();
        wz.calculate_WZ("wzmr80050001000024");
    }
    
    public void calculate_WZ(String plate_id) {

        ArrayList<WZ> wheat_zoomer_total = new ArrayList<>();

        String[] test_code_list = {"WZPOS", "WZNEG", "AGIGG", "AGIGA", "ABGIGG", "ABGIGA", "GGIGG", "GGIGA", "OGIGG",
         "OGIGA", "AZIGG", "AZIGA", "AAIGG", "AAIGA", "HMWIGG", "HMWIGA", "LMWIGG", "LMWIGA", "GIGG", "GIGA", "PRIGG", 
         "PRIGA", "SEIGG", "SEIGA", "FIGG", "FIGA", "APIGG", "APIGA", "GLOIGG", "GLOIGA", "PUIGG", "PUIGA", "SOIGG", 
         "SOIGA", "WGIGG", "WGIGA", "T3IGG", "T3IGA", "T6IGG", "T6IGA"};
        //String[] test_code_list = {"AGIGG","AGIGA"};
        String[] test_id_list = {"194", "195", "196", "197", "198", "199", "200", "201", "204", "244", "245", "246", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "229", "230", "231", "232"};
        String[] test_id_threshold = {"6834.882743", "2107.625943", "9910.444545", "2814.384091", "7293.369496", "2223.104095", "11469.3265", "3124.815353", "4032.24125", "1397.01", "5780.220388", "2231.631509", "36164.48875", "6633.18125", "12127.5016", "2806.6587", "19915.95125", "3981.385", "14139.52", "2735.1425", "7814.251591", "2712.046619", "12259.73514", "3170.814028", "8579.530199", "2949.000994", "8133.432545", "2589.159711", "8209.130105", "2597.617325", "5420.19625", "1991.66125", "7843.119513", "2575.653539", "7184.968587", "2357.596467", "7717.876837", "2635.046633"};
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.10.121/tsp_test_unit_data?useSSL=false", "TSPI3", "000028");
            Statement sql_statement = con.createStatement();
            Statement sql_statement2 = con.createStatement();
            // Get all samples for given plate id
            String get_samples = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'chip_analysis_result_new' and table_name like \"" + plate_id + "%\"";
            ResultSet rs = sql_statement.executeQuery(get_samples);
            if (!rs.isBeforeFirst()) {
                System.out.println("Plate id does not exist !!!");
            }
            //ResultSet rs2;
            while (rs.next()) {
                // Get individual table name in the plate
                String table_name = rs.getString(1);
                // Create new list for each sample id
                ArrayList<WZTest> wheat_zoomer_test = new ArrayList<>();
                // Create new class for each sample id
                WZ wz_total = new WZ();
                // Get sample id
                wz_total.sample_id = table_name;
                String table_full_name = "`chip_analysis_result_new`.`" + table_name + "`";
                for (int i = 0; i < test_code_list.length; i++) {
                    // Create new class for each calculation
                    WZTest w = new WZTest();
                    // Get test code
                    String test_code = test_code_list[i];
                    switch (test_code) {
                        // <editor-fold defaultstate="collapsed">
                        case "AGIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[0];

                            // Calculation step
                            String calculation_agigg = "select avg(t.total)/" + test_id_threshold[0] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'alpha gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            System.out.println(calculation_agigg);
                            ResultSet rs2 = sql_statement2.executeQuery(calculation_agigg);

                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();

                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "AGIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[1];

                            // Calculation step
                            String calculation_agiga = "select avg(t.total)/" + test_id_threshold[1] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'alpha gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_agiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;
                        case "ABGIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[2];

                            // Calculation step
                            String calculation_abgigg = "select avg(t.total)/" + test_id_threshold[2] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'alpha/beta gliadin (Fragment)' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_abgigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "ABGIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[3];

                            // Calculation step
                            String calculation_abgiga = "select avg(t.total)/" + test_id_threshold[3] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'alpha/beta gliadin (Fragment)' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_abgiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "GGIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[4];

                            // Calculation step
                            String calculation_ggigg = "select avg(t.total)/" + test_id_threshold[4] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'gamma gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_ggigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "GGIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[5];

                            // Calculation step
                            String calculation_ggiga = "select avg(t.total)/" + test_id_threshold[5] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'gamma gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_ggiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "OGIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[6];

                            // Calculation step
                            String calculation_ogigg = "select avg(t.total)/" + test_id_threshold[6] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'omega gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_ogigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "OGIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[7];

                            // Calculation step
                            String calculation_ogiga = "select avg(t.total)/" + test_id_threshold[7] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'omega gliadin' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_ogiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "AZIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[8];

                            // Calculation step
                            String calculation_azigg = "select avg(t.total)/" + test_id_threshold[8] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'zonulin%' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_azigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "AZIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[9];

                            // Calculation step
                            String calculation_aziga = "select avg(t.total)/" + test_id_threshold[9] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'zonulin%' and antigen_class like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_aziga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "AAIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[10];

                            // Calculation step
                            String calculation_aaigg = "select avg(t.total)/" + test_id_threshold[10] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%actin%' and Antigen_major_type like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_aaigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "AAIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[11];

                            // Calculation step
                            String calculation_aaiga = "select avg(t.total)/" + test_id_threshold[11] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%actin%' and Antigen_major_type like 'gluten sensitivity' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_aaiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "HMWIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[12];

                            // Calculation step
                            String calculation_hmwigg = "select avg(t.total)/" + test_id_threshold[12] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'hmw_glutenin' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_hmwigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "HMWIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[13];

                            // Calculation step
                            String calculation_hmwiga = "select avg(t.total)/" + test_id_threshold[13] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'hmw_glutenin' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_hmwiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "LMWIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[14];

                            // Calculation step
                            String calculation_lmwigg = "select avg(t.total)/" + test_id_threshold[14] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'lmw_glutenin' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_lmwigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "LMWIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[15];

                            // Calculation step
                            String calculation_lmwiga = "select avg(t.total)/" + test_id_threshold[15] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like 'lmw_glutenin' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_lmwiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "GIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[16];

                            // Calculation step
                            String calculation_gigg = "select avg(t.total)/" + test_id_threshold[16] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%gliadorphin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_gigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "GIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[17];

                            // Calculation step
                            String calculation_giga = "select avg(t.total)/" + test_id_threshold[17] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%gliadorphin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_giga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "PRIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[18];

                            // Calculation step
                            String calculation_prigg = "select avg(t.total)/" + test_id_threshold[18] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%prodynorphin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_prigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "PRIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[19];

                            // Calculation step
                            String calculation_priga = "select avg(t.total)/" + test_id_threshold[19] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%prodynorphin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_priga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "SEIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[20];

                            // Calculation step
                            String calculation_seigg = "select avg(t.total)/" + test_id_threshold[20] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%serpin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_seigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "SEIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[21];

                            // Calculation step
                            String calculation_seiga = "select avg(t.total)/" + test_id_threshold[21] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%serpin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_seiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "FIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[22];

                            // Calculation step
                            String calculation_figg = "select avg(t.total)/" + test_id_threshold[22] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%farinin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_figg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "FIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[23];

                            // Calculation step
                            String calculation_figa = "select avg(t.total)/" + test_id_threshold[23] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%farinin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_figa);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "APIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[24];

                            // Calculation step
                            String calculation_apigg = "select avg(t.total)/" + test_id_threshold[24] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%amylase%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_apigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "APIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[25];

                            // Calculation step
                            String calculation_apiga = "select avg(t.total)/" + test_id_threshold[25] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%amylase%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_apiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "GLOIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[26];

                            // Calculation step
                            String calculation_gloigg = "select avg(t.total)/" + test_id_threshold[26] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%globulin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_gloigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "GLOIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[27];

                            // Calculation step
                            String calculation_gloiga = "select avg(t.total)/" + test_id_threshold[27] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%globulin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_gloiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "PUIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[28];

                            // Calculation step
                            String calculation_puigg = "select avg(t.total)/" + test_id_threshold[28] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%peroxiredoxin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_puigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "PUIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[29];

                            // Calculation step
                            String calculation_puiga = "select avg(t.total)/" + test_id_threshold[29] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%peroxiredoxin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_puiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "SOIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[30];

                            // Calculation step
                            String calculation_soigg = "select avg(t.total)/" + test_id_threshold[30] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%somatostatin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_soigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "SOIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[31];

                            // Calculation step
                            String calculation_soiga = "select avg(t.total)/" + test_id_threshold[31] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%somatostatin%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_soiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "WGIGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[32];

                            // Calculation step
                            String calculation_wgigg = "select avg(t.total)/" + test_id_threshold[32] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%agglut%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_wgigg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "WGIGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[33];

                            // Calculation step
                            String calculation_wgiga = "select avg(t.total)/" + test_id_threshold[33] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where protein_names like '%agglut%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_wgiga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "T3IGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[34];

                            // Calculation step
                            String calculation_t3igg = "select avg(t.total)/" + test_id_threshold[34] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where gene_name like '%tgm3%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation_t3igg);
                            rs2 = sql_statement2.executeQuery(calculation_t3igg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "T3IGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[35];

                            // Calculation step
                            String calculation_t3iga = "select avg(t.total)/" + test_id_threshold[35] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where gene_name like  '%tgm3%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_t3iga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "T6IGG":
                            w.test_code = test_code;
                            w.test_id = test_id_list[36];

                            // Calculation step
                            String calculation_t6igg = "select avg(t.total)/" + test_id_threshold[36] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanIntensity order by " + table_name + ".MeanIntensity desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where gene_name like  '%tgm6%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_t6igg);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }

                            wheat_zoomer_test.add(w);
                            break;

                        case "T6IGA":
                            w.test_code = test_code;
                            w.test_id = test_id_list[37];

                            // Calculation step
                            String calculation_t6iga = "select avg(t.total)/" + test_id_threshold[37] + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where gene_name like  '%tgm6%' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_t6iga);
                            while (rs2.next()) {
                                w.test_result = rs2.getString(1);
                            }
                            rs2.close();
                            if (!(w.test_result != null && !w.test_result.isEmpty())) {
                                w.test_error = "No data exists";

                            } else {
                                if (Float.valueOf(w.test_result) < 0.05) {
                                    w.test_error = "Value Low Range";
                                } else {
                                    w.test_error = "0";
                                }
                            }
                            wheat_zoomer_test.add(w);
                            break;

                        case "WZPOS":
                            w.test_code = test_code;
                            w.test_id = "WZPOS";

                            // Calculation step
                            String calculation_wzpos = "select avg(t.total)" + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where sequence like  'teqpeqpfpeqpteqpeqpfpeqpteqpeqpfpeqp' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_wzpos);
                            while (rs2.next()) {
                                String result = rs2.getString(1);
                                if (!(result != null && !result.isEmpty())) {
                                    w.test_error = "No data exists";

                                } else {
                                    if (Float.valueOf(result) > 3000) {
                                        w.test_result = "QC POS PASS";
                                        w.test_error = "0";

                                    } else {
                                        w.test_result = "QC POS FAIL";
                                        w.test_error = "QC FAIL";
                                    }

                                }
                            }
                            rs2.close();
                            wheat_zoomer_test.add(w);
                            break;

                        case "WZNEG":
                            w.test_code = test_code;
                            w.test_id = "WZNEG";

                            // Calculation step
                            String calculation_wzneg = "select avg(t.total)" + " from (select substring_index(substring_index(group_concat(" + table_full_name
                                    + ".MeanAlex647 order by " + table_name + ".MeanAlex647 desc), ',',ceil(count(*)/2)),',', -1) as total from " + table_full_name
                                    + ",`greatwall_sequence_sh`.`03_chip_celiac_mask` where sequence like  'teqpeqpfpeqpteqpeqpfpeqpteqpeqpfpeqp' and "
                                    + table_full_name + ".row=`greatwall_sequence_sh`.`03_chip_celiac_mask`.row and " + table_full_name + ".col=`greatwall_sequence_sh`.`03_chip_celiac_mask`.col "
                                    + "group by `greatwall_sequence_sh`.`03_chip_celiac_mask`.sequence) t";
                            //System.out.println(calculation);
                            rs2 = sql_statement2.executeQuery(calculation_wzneg);
                            while (rs2.next()) {
                                String result = rs2.getString(1);
                                if (!(result != null && !result.isEmpty())) {
                                    w.test_error = "No data exists";

                                } else {
                                    if (Float.valueOf(result) < 3000) {
                                        w.test_result = "QC NEG PASS";
                                        w.test_error = "0";

                                    } else {
                                        w.test_result = "QC NEG FAIL";
                                        w.test_error = "QC FAIL";
                                    }

                                }
                            }
                            rs2.close();
                            wheat_zoomer_test.add(w);
                            break;// </editor-fold>
                        }
                    // Add element for each test with test results and sample id

                }
                wz_total.wheat_zoomer = wheat_zoomer_test;
                wheat_zoomer_total.add(wz_total);
            }
            con.close();

        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException");
            System.err.println(e.getMessage());

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }

//        return save_WZ_to_DB(wheat_zoomer_total, plate_id);

    }

    /**
     * @param args the command line arguments
     */
//    private boolean save_WZ_to_DB(ArrayList<WZ> wheat_zoomer_results, String pID, EventLog appLogger) {
//
//        boolean returnValue = true;
//
//        DataBaseUtility dbUtility_TSP = new DataBaseUtility("", TSPPackageConstants.TSP_SERVER);
//
//        ResultSet res = null;
//
//        Pillar_Plate_Info PPI = new Pillar_Plate_Info(pID, appLogger);
//
//        HashMap<String, Well_Info> sampleMap = PPI.getWellPlateInfo().getWellInfoMap();
//
//        String sql_query = "INSERT INTO "+TSPSystemConstants.TSP_WHEATZOOMER_RAW_RESULT
//                + "(`julien_barcode`,`plate_id`, `WZPOS`, `WZNEG`,`AGIGG`, `AGIGA`, `ABGIGG`, `ABGIGA`, `GGIGG`, `GGIGA`, `OGIGG`, `OGIGA`, `AZIGG`, `AZIGA`, `AAIGG`, `AAIGA`, `HMWIGG`, `HMWIGA`, `LMWIGG`, `LMWIGA`, `GIGG`, `GIGA`, `PRIGG`, `PRIGA`, `SEIGG`, `SEIGA`, `FIGG`, `FIGA`, `APIGG`, `APIGA`, `GLOIGG`, `GLOIGA`, `PUIGG`, `PUIGA`, `SOIGG`, `SOIGA`, `WGIGG`, `WGIGA`, `T3IGG`, `T3IGA`, `T6IGG`, `T6IGA`) \n"
//                + "VALUES "
//                + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
//                + " ON DUPLICATE KEY UPDATE `WZPOS`=?,`WZNEG`=?,`AGIGG`=?,`AGIGA`=?,`ABGIGG`=?,`ABGIGA`=?,`GGIGG`=?,`GGIGA`=?,`OGIGG`=?,`OGIGA`=?,`AZIGG`=?,`AZIGA`=?,`AAIGG`=?,`AAIGA`=?,`HMWIGG`=?,`HMWIGA`=?,`LMWIGG`=?,`LMWIGA`=?,`GIGG`=?,`GIGA`=?,`PRIGG`=?,`PRIGA`=?,`SEIGG`=?,`SEIGA`=?,`FIGG`=?,`FIGA`=?,`APIGG`=?,`APIGA`=?,`GLOIGG`=?,`GLOIGA`=?,`PUIGG`=?,`PUIGA`=?,`SOIGG`=?,`SOIGA`=?,`WGIGG`=?,`WGIGA`=?,`T3IGG`=?,`T3IGA`=?,`T6IGG`=?,`T6IGA`=?;";
//
//        dbUtility_TSP.changeSQL(sql_query);
//
//        for (WZ wz : wheat_zoomer_results) {
//
//            String sampleIndexwithPlateID = wz.sample_id;
//            int delimiterIndex = sampleIndexwithPlateID.indexOf("_");
//            String sampleIndex = sampleIndexwithPlateID.substring(delimiterIndex + 1);
//
//            sampleIndex = sampleIndex.toUpperCase();
//            Well_Info wellInfo = sampleMap.get(sampleIndex);
//
//            if (wellInfo != null) {
//                String sampleBarcode = wellInfo.getSampleBarcode();
//
//                dbUtility_TSP.setString(1, sampleBarcode);
//                dbUtility_TSP.setString(2, pID);
//
//                ArrayList<WZTest> wzList = wz.wheat_zoomer;
//
//                //Set insert values
//                for (int i = 0; i < wzList.size(); i++) {
//
//                    String result = wzList.get(i).test_result;
//                    String error = wzList.get(i).test_error;
//
//                    if (error.equals("0")) {
//                        dbUtility_TSP.setString(i + 3, result);
//                    } else {
//                        dbUtility_TSP.setString(i + 3, error);
//                    }
//                }
//
//                //Set update values
//                for (int i = 0; i < wzList.size(); i++) {
//
//                    String result = wzList.get(i).test_result;
//                    String error = wzList.get(i).test_error;
//
//                    if (error.equals("0")) {
//                        dbUtility_TSP.setString(i + 43, result);
//                    } else {
//                        dbUtility_TSP.setString(i + 43, error);
//                    }
//                }
//
//                if (dbUtility_TSP.executeUpdate() != 1) {
//                    returnValue = false;
//                }
//
//            } else {
//                //System.out.println("Can't find sample barcode for given sample index:"+sampleIndex+" in Plate:"+pID);
//                appLogger.errorLog("Can't find sample barcode for given sample index:"+sampleIndex+" in Plate:"+pID, "");
//            }
//
//        }
//        
//        dbUtility_TSP.close();
//
//        return returnValue;
//    }



}
