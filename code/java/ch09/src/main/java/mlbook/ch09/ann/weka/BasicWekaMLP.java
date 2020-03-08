package mlbook.ch09.ann.weka;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.Utils;

public class BasicWekaMLP {

    public BasicWekaMLP() {
        try {
            FileReader fr = new FileReader("/Users/jasebell/bookwork/mlbook2ndedition/data/ch09/weka/vehicledata.arff");

            Instances training = new Instances(fr);

            training.setClassIndex(training.numAttributes() -1);

            MultilayerPerceptron mlp = new MultilayerPerceptron();
            mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H 4"));


            mlp.buildClassifier(training);

            FileReader tr = new FileReader("/Users/jasebell/bookwork/mlbook2ndedition/data/ch09/weka/testdata.arff");
            Instances testdata = new Instances(tr);
            testdata.setClassIndex(testdata.numAttributes() -1);

            Evaluation eval = new Evaluation(training);
            eval.evaluateModel(mlp, testdata);
            System.out.println(eval.toSummaryString("\nResults\n*******\n", true));

            tr.close();
            fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BasicWekaMLP mlp = new BasicWekaMLP();
    }

}

