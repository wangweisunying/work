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

//INSERT INTO `tsp_test_unit_data`.`test_unit_data` (`test_name`, `julien_barcode`, `unit`, `pillar_plate_id`, `row`, `col`) 
//values ('ALPSIGG_IGM_unit',1805240210,43.207004067099,'ALPG80010280000156','A',2);
public class Unit{
    String test_name, julien_barcode,pillar_plate_id ,row;
    double unit;
    int col;
    Unit(String test_name,String julien_barcode,double unit,String pillar_plate_id ,String row,int col){
        this.test_name = test_name;
        this.julien_barcode = julien_barcode;
        this.pillar_plate_id = pillar_plate_id;
        this.row = row;
        this.unit = unit;
        this.col = col;
    }
    void testPrint(){
        System.out.println(test_name + " " + julien_barcode + " " + pillar_plate_id + " " + row + " " + col + " " + unit );
    }
}


