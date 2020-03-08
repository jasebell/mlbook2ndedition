package mlbook.ch13.spark;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.apache.spark.SparkConf;

public class BasicMLLibFPGrowth {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("BasicMLLibFPGrowth");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> data = sc.textFile("/Users/jasebell/bookwork/mlbook2ndedition/data/ch13/mllib/fpgrowth_items.txt");

        JavaRDD<List<String>> basketItems = data.map(line -> Arrays.asList(line.split(" ")));

        FPGrowth fpg = new FPGrowth()
                .setMinSupport(0.2)
                .setNumPartitions(10);
        FPGrowthModel<String> model = fpg.run(basketItems);

        for (FPGrowth.FreqItemset<String> itemset: model.freqItemsets().toJavaRDD().collect()) {
            System.out.println("(" + itemset.javaItems() + "), " + itemset.freq());
        }

        double minConfidence = 0.7;
        for (AssociationRules.Rule<String> rule
                : model.generateAssociationRules(minConfidence).toJavaRDD().collect()) {
            System.out.println(
                    rule.javaAntecedent() + " => " + rule.javaConsequent() + ", " + rule.confidence());
        }
        sc.stop();
    }
}
