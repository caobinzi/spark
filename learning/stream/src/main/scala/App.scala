
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.log4j.{Level, Logger}

object MyApp {
  def main(args: Array[String]) {

    // Create the context with a 1 second batch size
    val sparkConf =
      new SparkConf().setAppName("NetworkWordCount")
        .setMaster("local[4]")

    val ssc = new StreamingContext(sparkConf, Seconds(1))
    Logger.getRootLogger.setLevel(Level.ERROR)

    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    val words = lines.foreachRDD{
      rdd => rdd.foreach(x => println(s"I recevied: ${x}"))
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
