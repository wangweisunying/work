/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wei Wang
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class ZIG_AUTO {
    private class ZigData{
        public String pillarId ,julien_barcode,row;
        public double signal;
        public int col;
        public ZigData(String pillarId,String julien_barcode,String row,int col,double signal){
            this.pillarId = pillarId;
            this.julien_barcode = julien_barcode;
            this.row = row;
            this.signal = signal;
            this.col = col;
        }
    }   
    String dbName = DataBaseInfo.name_V7,dbUser = DataBaseInfo.user_V7,dbPW = DataBaseInfo.pw_V7;
    public static void main(String args[]){   
        ZIG_AUTO zigAuto = new ZIG_AUTO();
        zigAuto.run("ZIG201808241");
    }
    
    public void run(String well_id){
        ArrayList<ZigData> list = new ArrayList();
        ArrayList<Double> signalRaw = new ArrayList();
        HashMap< String , Double > map = new HashMap();
        String[] ctrlPoints = {"A1" , "A12" , "G12" , "H1" , "H12"};
        for(String x : ctrlPoints){
            map.put(x , -1.0 );
        }
        //julienbarcode , pre_unit
        ArrayList<String> dupData = new ArrayList(DataBaseTool.getZigDup()); 
        
  

        String sql = "select pillar_plate_id,julien_barcode,`row`,`col`,`signal` from\n" +
"(select * from `vibrant_test_tracking`.well_info where well_plate_id = '"+well_id+"' ) as a\n" +
"join (select * from vibrant_test_raw_data.zonulin where pillar_plate_id  = (select  pillar_plate_id from vibrant_test_tracking.pillar_plate_info where well_plate_id = '"+well_id+"'  limit 1 )) as b\n" +
"on concat(a.well_row,a.well_col) = concat(b.row,b.col);";
        System.out.println(sql);
        try{
            Connection mycon = DriverManager.getConnection(dbName, dbUser, dbPW);
            Statement stmt = mycon.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String curloc = rs.getString("row") + rs.getInt("col");
                if(!map.keySet().contains(curloc)){
                    double curRaw = rs.getDouble("signal");
                    signalRaw.add(curRaw);
                    list.add(new ZigData(rs.getString("pillar_plate_id"),rs.getString("julien_barcode"),rs.getString("row"),rs.getInt("col"), curRaw)); 
                }
                else{
                    System.out.println(rs.getString("row") + rs.getInt("col") + " : " + rs.getDouble("signal"));
                    map.put(curloc , rs.getDouble("signal"));
                }
            }
            if(signalRaw.size() == 0){
                System.out.println("data not stitched!");
                return;
            }
            System.out.println(signalRaw);
            //get the percentile
            CalTool calTool = new CalTool();
            double x1 = calTool.getPercentile(signalRaw, 0.15);
            double y1 = 45.2;
            double x2 = 65535;
            double y2 = 10;
            double slope = (y2 - y1) / (x2 - x1);
            double interception = (x2 * y1 - x1 * y2)/(x2 - x1);
            for(String x : map.keySet()){
                map.put( x , map.get(x) * slope + interception);
            }
            
            
            for (ZigData cur : list) {
                if(cur.signal != -1){
                    if(!dupData.contains(cur.julien_barcode)){
                        cur.signal = cur.signal * slope + interception;
                    } 
                    else{
//                        System.out.println(cur.julien_barcode + " cursig" +cur.signal+" after" + Double.parseDouble(dupData.get(dupData.indexOf(cur.julien_barcode)+1)));
                        cur.signal = Double.parseDouble(dupData.get(dupData.indexOf(cur.julien_barcode)+1));
                    } 
                }
                sql = "insert into `tsp_test_unit_data`.`test_unit_data` (`test_name`,`julien_barcode`,`unit`,`pillar_plate_id`,`row`,`col`,`time`) values ('ZIG_unit','"+cur.julien_barcode+"','"+cur.signal+"','"+cur.pillarId+"','"+cur.row+"',"+cur.col+",now()) on duplicate key update `unit` ="+cur.signal+";";
                stmt.executeUpdate(sql);
//                System.out.println(sql);
            }
            
            stmt.executeUpdate("insert into tsp_test_qc_data.test_qc_data (`test_name`,`pillar_plate_id`,`cal_1`,`pos_ctrl_1`,`neg_ctrl_1`,`TIME`) values('zig','"+list.get(0).pillarId+"','" + map.get("G12") +"','" + map.get("G12") +"','"+ (map.get("A12") - 8) +"',now());");
            System.out.println("insert into tsp_test_qc_data.test_qc_data (`test_name`,`pillar_plate_id`,`cal_1`,`pos_ctrl_1`,`neg_ctrl_1`,`TIME`) values('zig','"+list.get(0).pillarId+"','" + map.get("G12") +"','" + map.get("G12") +"','"+ (map.get("A12") - 8) +"',now());");
            stmt.executeUpdate("UPDATE `vibrant_test_tracking`.`pillar_plate_info` SET `status`='finish' WHERE `pillar_plate_id`='"+list.get(0).pillarId+"';");        
            
            System.out.println("done!");
        }
        catch(Exception ex){
            ex.printStackTrace();
        } 
    } 
}
