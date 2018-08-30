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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author wangw
 */
public class Tools_Calculate {

    /**
     * @param args the command line arguments
     */
    public static double findMedian(double[] arr) {
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

    public static double findMedian(ArrayList<Double> list) {
        double median;
        Collections.sort(list);
        if (list.size() % 2 == 1) {
            median = list.get((list.size() - 1) / 2);
        } else {
            median = (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2.00;
        }

        return median;
    }

    public static double mean(ArrayList<Double> a) {
        double sum = sum_float(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0f);
        return mean;
    }
 

    public static double std(ArrayList<Double> a) {
        float sum = 0;
        double avg = mean(a);

        for (int i = 0; i <= a.size() - 1; i++) {
            sum += Math.pow((a.get(i) - avg), 2);
        }
        return (double) Math.sqrt(sum / (a.size() - 1)); // sample
    }

    public static double sum_float(ArrayList<Double> a) {
        if (a.size() > 0) {
            float sum = 0;

            for (int i = 0; i <= a.size() - 1; i++) {
                sum += a.get(i);
            }
            return sum;
        }
        return 0;
    }

}
