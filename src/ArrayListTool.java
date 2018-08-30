
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
public class ArrayListTool {
    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<String>();
        list.add("a1");
        list.add("a2");
        list.add("a3");
        list.add("a4");
        list.add("a5");
        list.add("a6");
        
        System.out.println(to_sql_in(list));
    }
    public static String to_sql_in(ArrayList<String> list){
        String res = "";
        if(list.isEmpty()){
            return "";
        }
        else{
            for(String s:list){
                res+="'"+s+"',";
            }
            return res.substring(0,res.length()-1);
        }

    }
}
