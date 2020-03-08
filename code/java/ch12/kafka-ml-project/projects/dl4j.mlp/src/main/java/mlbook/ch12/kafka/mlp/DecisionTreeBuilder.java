package mlbook.ch12.kafka.mlp;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.UUID;

public class DecisionTreeBuilder {

    public DecisionTreeBuilder() {

        String uuid = UUID.randomUUID().toString();


        // build the classifier
        J48 cls= buildModel(uuid);

        // persist the model to the file system
        persistModel(cls, uuid);

    }

    public J48 buildModel(String uuid) {

        //Classifier cls = new J48();
        J48 cls = new J48();
        try {
            Instances inst = new Instances(new BufferedReader(new FileReader("/opt/mlbook/testdata/alldata.arff")));
            inst.setClassIndex(inst.numAttributes() - 1);
            try {
                long start = System.currentTimeMillis();
                cls.buildClassifier(inst);

                Evaluation evaluation = new Evaluation(inst);
                Random rand = new Random(1);
                int folds = 10;

                evaluation.crossValidateModel(cls, inst, folds, rand);
                long stop = System.currentTimeMillis();

                DBTools.writeResultsToDB(uuid, -1, (stop - start), evaluation.pctCorrect()/100, evaluation.toSummaryString(), "dtr");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cls;

    }

    public void persistModel(J48 cls, String uuid) {
        try {
            SerializationHelper.write("/opt/mlbook/testdata/models/" + uuid+ ".model", cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DecisionTreeBuilder ddt = new DecisionTreeBuilder();
    }
}
