package mlbook.ch13.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.regex.Pattern;

public class BasicSparkStreaming {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {
        String hostname = "192.168.1.103";
        int port = 9999;


        SparkConf sparkConf = new SparkConf().setAppName("BasicSparkStreaming");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));


        JavaReceiverInputDStream<String> lines = ssc.socketTextStream(
                hostname, port, StorageLevels.MEMORY_AND_DISK_SER);

        JavaDStream<String> wordsOnEachLine = lines.flatMap(line -> Arrays.asList(SPACE.split(line)).iterator());
        JavaPairDStream<String, Integer> wordCounts = wordsOnEachLine.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);

        wordCounts.print();
        ssc.start();
        ssc.awaitTermination();
    }
}
