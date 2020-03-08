package mlbook.ch04.examples;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SampleStats {

    // Example 1 - Loading the file and converting to an Integer collection.
    public List<Integer> loadFile(String filename) throws Exception {
        List<Integer> numList = new ArrayList<Integer>();
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        while ((s = br.readLine()) != null) {
            numList.add(Integer.parseInt(s));
        }
        return numList;
    }


    public List<Double> loadFileToDouble(String filename) throws Exception {
        List<Double> numList = new ArrayList<Double>();
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        while ((s = br.readLine()) != null) {
            numList.add(Double.parseDouble(s));
        }
        return numList;
    }

    // Exercise 2 - Finding Min and Max values of the collection.
    public double getMinValue(List<Double> nums) {
        return Collections.min(nums);
    }

    public double getMaxValue(List<Double> nums) {
        return Collections.max(nums);
    }

    public int getSum(List<Integer> nums) {
        int total = 0;
        for (Integer i : nums) {
            total += i.intValue();
        }
        return total;
    }

    public double getSumWithArrays(List<Double> nums) {
        double[] pNumList = nums.stream()
                             .mapToDouble(Double::doubleValue)
                             .toArray();
        return Arrays.stream(pNumList).sum();
    }

    public double getMean(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        return StatUtils.mean(pNumList);
    }

    public double getHarmonicMean(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        double reciprocolTotal = 0.0;
        for(int i = 0 ; i < pNumList.length - 1 ; i++) {
            reciprocolTotal += 1/pNumList[i];
        }
        double harmonicMean = pNumList.length/reciprocolTotal;
        return harmonicMean;
    }

    public double getGeometricMean(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        return StatUtils.geometricMean(pNumList);
    }

    public double[] getMode(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        return StatUtils.mode(pNumList);
    }

    public double getMeadian(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        DescriptiveStatistics ds = new DescriptiveStatistics();
        for(int i = 0; i < pNumList.length -1 ; i++ ) {
            ds.addValue(pNumList[i]);
        }
        return ds.getPercentile(50);
    }

    public double getRange(List<Double> nums) {
        return (getMaxValue(nums) - getMinValue(nums));
    }

    public double getIQR(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        DescriptiveStatistics ds = new DescriptiveStatistics();
        for(int i = 0; i < pNumList.length -1 ; i++ ) {
            ds.addValue(pNumList[i]);
        }
        return ds.getPercentile(75) - ds.getPercentile(25);
    }

    public double getVariance(List<Double> nums){
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        SummaryStatistics ss = new SummaryStatistics();
        for(int i = 0; i < pNumList.length -1 ; i++ ) {
            ss.addValue(pNumList[i]);
        }
        return ss.getVariance();
    }

    public double getStandardDeviation(List<Double> nums) {
        double[] pNumList = nums.stream().mapToDouble(Double::doubleValue).toArray();
        SummaryStatistics ss = new SummaryStatistics();
        for(int i = 0; i < pNumList.length -1 ; i++ ) {
            ss.addValue(pNumList[i]);
        }
        return ss.getStandardDeviation();
    }

    public static void main(String[] args) throws Exception {
        List<Integer> nums = new SampleStats().loadFile("/Users/jasebell/bookwork/mlbook2ndedition/data/ch04/stats.txt");
        List<Double> numsd = new SampleStats().loadFileToDouble("/Users/jasebell/bookwork/mlbook2ndedition/data/ch04/stats.txt");

        System.out.println(nums);

        int minVal = Collections.min(nums);
        int maxVal = Collections.max(nums);
        int total = new SampleStats().getSum(nums);
        double total2 = new SampleStats().getSumWithArrays(numsd);
        double avg = (double)total2/nums.size();
        double mean = new SampleStats().getMean(numsd);
        double hmean = new SampleStats().getHarmonicMean(numsd);
        double gmean = new SampleStats().getGeometricMean(numsd);
        double[] mode = new SampleStats().getMode(numsd);
        double median = new SampleStats().getMeadian(numsd);
        double iqr = new SampleStats().getIQR(numsd);
        double variance = new SampleStats().getVariance(numsd);
        double sd = new SampleStats().getStandardDeviation(numsd);


        System.out.println("Minimum value: " + minVal);
        System.out.println("Maximum value: " + maxVal);
        System.out.println("Total value (getSum): " + total);
        System.out.println("Total value (getSumWithArrays): " + total2);
        System.out.println("Mean value (manual method): " + avg);
        System.out.println("Mean value (getMean): " + mean);
        System.out.println("Harmonic mean value (getHarmonicMean): " + hmean);
        System.out.println("Geometric mean value (getGeometricMean): " + gmean);
        System.out.println("Mode (getMode): " + mode);
        System.out.println("Median (getMedian): " + median);
        System.out.println("IQR (getIQR): " + iqr);
        System.out.println("Variance (getVariance): " + variance);
        System.out.println("Standard Deviation (getStandardDeviation): " + sd);


    }
}

