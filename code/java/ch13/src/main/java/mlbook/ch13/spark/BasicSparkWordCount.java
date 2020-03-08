package mlbook.ch13.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class BasicSparkWordCount {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {
        System.out.println("Starting BasicSparkWordCount....");
        if (args.length < 1) {
            System.err.println("Usage: BasicSparkWordCount <file>");
            System.exit(1);
        }

        SparkSession sparkSession = SparkSession
                .builder()
                .appName("BasicSparkWordCount")
                .getOrCreate();

        JavaRDD<String> linesOfText = sparkSession.read().textFile(args[0]).javaRDD();
        JavaRDD<String> wordsInEachLine = linesOfText.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
        JavaPairRDD<String, Integer> allTheOnes = wordsInEachLine.mapToPair(singleWord -> new Tuple2<>(singleWord, 1));
        JavaPairRDD<String, Integer> finalCounts = allTheOnes.reduceByKey((i1, i2) -> i1 + i2);

        List<Tuple2<String, Integer>> output = finalCounts.collect();
        for (Tuple2<?, ?> tuple : output) {
            System.out.println("Segment " + tuple._1() + " found " + tuple._2() + " times.");
        }
        sparkSession.stop();
        System.out.println("Finishing BasicSparkWordCount....");
    }
}

