/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Wei Wang
 */
public class CalTool {

    /**
     * @param args the command line arguments
     */
    public boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        int tmp = (int) Math.sqrt(n);
        for (int i = 2; i <= tmp; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
// input Array

    public double findMedian(double[] arr) {
        double median;
        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));
        if (arr.length % 2 == 1) {
            median = arr[(arr.length - 1) / 2];
        } else {
            median = (arr[arr.length / 2] + arr[(arr.length / 2) - 1]) / 2.00;
        }

        return median;
    }
// input list

    public double findMedian(ArrayList<Double> list) {
        double median;
        Collections.sort(list);
        if (list.size() % 2 == 1) {
            median = list.get((list.size() - 1) / 2);
        } else {
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2.00;
        }

        return median;
    }

    public double getPercentile(double[] data, double p) {
        int n = data.length;
        Arrays.sort(data);
        double px = p * (n - 1);
        int i = (int) java.lang.Math.floor(px);
        double g = px - i;
        if (g == 0) {
            return data[i];
        } else {
            return (1 - g) * data[i] + g * data[i + 1];
        }
    }

    public double getPercentile(ArrayList<Double> list, double p) {
        int n = list.size();
        Collections.sort(list);
        double px = p * (n - 1);
        int i = (int) java.lang.Math.floor(px);
        double g = px - i;
        if (g == 0) {
            return list.get(i);
        } else {
            return (1 - g) * list.get(i) + g * list.get(i + 1);
        }
    }

    public double getPercentileRank(ArrayList<Double> list, double thres) {
        int size = list.size() - 1;
        list.add(thres);
        Collections.sort(list);
        int n = list.indexOf(thres);
        return ((list.get(n + 1) - thres) * (n - 1) + (thres - list.get(n - 1)) * n) / size;
    }
    public double getRandom(int ceil  , int floor){
        return ceil + Math.round(Math.random() * (floor - ceil)); 
    }
    

    public static void main(String[] args) {

        CalTool ct = new CalTool();
//        double[] arr = new double[]{2, 3, 4, 5, 7,6, 7, 7,8, 9, 11, 12, 13,100};
//        ArrayList<Double> list = new ArrayList();
//        for (double x : arr) {
//            list.add(x);
//        }
        System.out.println(Integer.parseInt("000"));
        
    }

//
}
