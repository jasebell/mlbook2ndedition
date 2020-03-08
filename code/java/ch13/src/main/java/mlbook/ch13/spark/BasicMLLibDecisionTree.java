package mlbook.ch13.spark;

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.util.MLUtils;

public class BasicMLLibDecisionTree {
    public static void main(String[] args) {

        int numberOfClasses = 2;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        String impurity = "gini";
        int maximumTreeDepth = 7;
        int maximumBins = 48;


        SparkConf sparkConf = new SparkConf().setAppName("BasicMLLibDecisionTree");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        String datapath = "/Users/jasebell/bookwork/mlbook2ndedition/data/ch13/mllib/dtree.txt";
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        DecisionTreeModel model = DecisionTree.trainClassifier(trainingData, numberOfClasses,
                categoricalFeaturesInfo, impurity, maximumTreeDepth, maximumBins);

        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
        double predictionTestErrorValue =
                predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();

        System.out.println("Prediction test error value: " + predictionTestErrorValue);
        System.out.println("Output classification tree:\n" + model.toDebugString());
    }
}


