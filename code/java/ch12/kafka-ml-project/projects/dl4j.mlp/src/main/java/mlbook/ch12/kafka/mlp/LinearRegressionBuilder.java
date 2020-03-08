package mlbook.ch12.kafka.mlp;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LinearRegressionBuilder {

    private static String path = "/opt/mlbook/testdata/alloutput.csv";



    public LinearRegressionBuilder() {
        List<String> lines = loadData(path);
        long start = System.currentTimeMillis();
        SimpleRegression sr = getLinearRegressionModel(lines);

        long stop = System.currentTimeMillis();
        long time = stop - start;

        String uuid = UUID.randomUUID().toString();
        DBTools.writeLinearResults(uuid, sr.getIntercept(), sr.getSlope(), sr.getRSquare(), time, runPredictions(sr, 20));
        runPredictions(sr, 40);
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
        // Display the slope standard error
        sb.append("Standard Error: " + sr.getSlopeStdErr());
        sb.append("\n");
        // Display adjusted R2 value
        sb.append("Adjusted R2 value: " + sr.getRSquare());
        sb.append("\n");
        sb.append("*************************************************");
        sb.append("\n");
        sb.append("Running random predictions......");
        sb.append("\n");
        sb.append("");
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
