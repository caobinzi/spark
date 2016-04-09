import scala.language.higherKinds
import scala.language.implicitConversions
import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import concurrent._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.sql.DataFrame
import org.apache.spark.streaming.dstream.DStream
import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

object WindowApp extends App {

  val sparkConf = new SparkConf().setAppName("SqlNetworkWordCount").setMaster("local[4]")
  val ssc = new StreamingContext(sparkConf, Seconds(2))
  ssc.checkpoint("checkpoint")
  val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092")
  val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set("test"))
  val lines = messages.map(_._2)
  val words = lines.flatMap(_.split(" "))
  val wordCounts = words.map(x => (x, 1L))
  val windowedWordCounts = wordCounts.
          reduceByKeyAndWindow(_ + _, _ - _, Seconds(10), Seconds(2), 2)
  windowedWordCounts.foreach {
    rdd => rdd.foreach {
      case (a, b) => println(s"${a} --> ${b}")
      
    }
  }

  // Start the computation
  ssc.start()
  ssc.awaitTermination()

}

