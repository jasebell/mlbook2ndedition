package mlbook.ch13.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

public class BasicMLLibKMeans {
        public static void main(String[] args) {

            SparkConf sparkConf = new SparkConf().setAppName("BasicMLLibKMeans");
            JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

            JavaRDD<String> data = sparkContext.textFile("/Users/jasebell/bookwork/mlbook2ndedition/data/ch13/mllib/kmeans.txt");
            JavaRDD<Vector> parsedData = data.map(s -> {
                String[] sarray = s.split(" ");
                double[] values = new double[sarray.length];
                for (int i = 0; i < sarray.length; i++) {
                    values[i] = Double.parseDouble(sarray[i]);
                }
                return Vectors.dense(values);
            });
            parsedData.cache();


            int numberOfClassClusters = 3;
            int numberOfIterations = 50;
            KMeansModel kMeansClusters = KMeans.train(parsedData.rdd(), numberOfClassClusters, numberOfIterations);

            double WSSSE = kMeansClusters.computeCost(parsedData.rdd());
            System.out.println("Set sum squared errors = " + WSSSE);

            double cost = kMeansClusters.computeCost(parsedData.rdd());
            System.out.println("Computed cost: " + cost);

            System.out.println("Showing cluster centres: ");
            for (Vector center: kMeansClusters.clusterCenters()) {
                System.out.println(" " + center);
            }

            sparkContext.stop();
        }
}
