/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author Wei Wang
 */
public class test {
    static class Axis{
        int x;
        int y;
        Axis(int x , int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public boolean equals(Object o){
            return this.x == ((Axis)o).x && this.y == ((Axis)o).y;
        }
        @Override
        public int hashCode(){
            return x *10000 + y;
        }
    }
    public static void main(String[] args){
        
          Queue list = new PriorityQueue(new Comparator(){
              @Override
              public int compare(Object o1, Object o2) {
                  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
              }
          });
//        TreeSet<Axis> set = new TreeSet(new Comparator<Axis>() {
//        @Override
//            public int compare(Axis o1, Axis o2) {
//               return o1.x == o2.x && o1.y == o2.y ? 0 : 1 ;
//            }
//        });

         

//        HashSet<Axis> set = new HashSet();
//        Axis x1 = new Axis(1 , 1);
//        Axis x2 = new Axis(1 , 1);
//        
//        set.add(x1);
//        set.add(x2);
//        
//        System.out.println(x1.equals(x2));
//        System.out.println(set);
//        

        
  
       test ts = new test();
       System.out.println(ts.subarraySum(new int[]{-2,0,0,1,-1,-1}));
     
    }
    public List<Integer> subarraySum(int[] nums) {
        List<Integer> res = new ArrayList();
        if(nums.length == 0){
            return res;
        }
        if(nums.length == 1 && nums[0] == 0){
            res.add(0);
            res.add(0);
        }
        boolean found = false;
        for(int i = 0 ; i < nums.length - 1 ; i ++){
            if(found){
                break;
            }
            int sum  = nums[i];
            if(sum == 0){
                res.add(i);
                res.add(i);
                break;
            }
            for(int j = i + 1 ; j < nums.length ; j ++){
                sum += nums[j];
                if(sum == 0){
                    res.add(i);
                    res.add(j);
                    found = true;
                    break;
                }
            }
        }
        return res;
    }
   
}
