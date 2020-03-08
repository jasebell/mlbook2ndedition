package mlbook.ch04.examples;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinearRegressionBuilder {
    private static String path = "/Users/jasebell/bookwork/mlbook2ndedition/data/ch04/ch4slr.csv";



    public LinearRegressionBuilder() {
        List<String> lines = loadData(path);
        SimpleRegression sr = getLinearRegressionModel(lines);
        System.out.println(runPredictions(sr, 10));
    }

    private SimpleRegression getLinearRegressionModel(List<String> lines) {
        SimpleRegression sr = new SimpleRegression();
        for(String s : lines) {
            String[] ssplit = s.split(",");
            double x = Double.parseDouble(ssplit[0]);
            double y = Double.parseDouble(ssplit[1]);
            sr.addData(x,y);
        }

        return sr;
    }

    private String runPredictions(SimpleRegression sr, int runs) {
        StringBuilder sb = new StringBuilder();
        // Display the intercept of the regression
        sb.append("Intercept: " + sr.getIntercept());
        sb.append("\n");
        // Display the slope of the regression.
        sb.append("Slope: " + sr.getSlope());
        sb.append("\n");
        sb.append("Running random predictions......");
        sb.append("\n");
        Random r = new Random();
        for (int i = 0 ; i < runs ; i++) {
            int rn = r.nextInt(10);
            sb.append("Input score: " + rn + " prediction: " + Math.round(sr.predict(rn)));
            sb.append("\n");
        }
        return sb.toString();
    }

    private List<String> loadData (String filename) {
        List<String> lines = new ArrayList<String>();
        try {
            FileReader f = new FileReader(filename);
            BufferedReader br;
            br = new BufferedReader(f);
            String line = "";
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }

        return lines;
    }

    public static void main(String[] args) {
        LinearRegressionBuilder dlr = new LinearRegressionBuilder();
    }

}
