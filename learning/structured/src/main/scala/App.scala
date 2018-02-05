
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.log4j.{Level, Logger}

object MyApp {
  def main(args: Array[String]) {

    // Create the context with a 1 second batch size
    val spark = SparkSession
    .builder
    .master("local")
    .appName("StructuredNetworkWordCount")
    .getOrCreate()
    import spark.implicits._

    val lines = spark.readStream
    .format("socket")
    .option("host", "localhost")
    .option("port", 9999)
    .load()
    val words = lines.as[String].flatMap(_.split(" "))

    // Generate running word count
    val wordCounts = words.groupBy("value").count()
    val query = wordCounts.writeStream
    .outputMode("complete")
    .format("console")
    .start()

    query.awaitTermination()
  }
}
