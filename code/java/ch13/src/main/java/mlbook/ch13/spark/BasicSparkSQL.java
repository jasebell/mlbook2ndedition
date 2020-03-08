package mlbook.ch13.spark;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.col;

public class BasicSparkSQL {

    private static String airportDataPath = "/Users/jasebell/bookwork/mlbook2ndedition/data/ch13/sql/airports.csv";

    public static void main(String[] args) throws AnalysisException {
        SparkSession spark = SparkSession
                .builder()
                .appName("ML Book Spark SQL Example")
                .getOrCreate();
        //runShowAirports(spark);
        //runShowIrishAirports(spark);
        runShowIrishAirportsByCols(spark);
        spark.stop();
    }


    public static void runShowAirports(SparkSession spark) throws AnalysisException {
        Dataset<Row> df = spark.read().csv(airportDataPath);
        df.show();
    }

    public static void runShowIrishAirports(SparkSession spark) throws AnalysisException {
        Dataset<Row> df = spark.read().csv(airportDataPath);
        df.createTempView("airports");
        Dataset<Row> irishAirports = spark.sql("SELECT _c1, _c4, _c5 FROM airports WHERE _c3='Ireland'");
        irishAirports.show();
    }

    public static void runShowIrishAirportsByCols(SparkSession spark) throws AnalysisException {
        Dataset<Row> df = spark.read().csv(airportDataPath);
        Dataset<Row> filtered = df.filter(col("_c3").contains("Ireland"));
        filtered.show();
    }

}
