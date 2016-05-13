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

object WindowApp extends App {

  def basic_rank() {
    val sql = """
    SELECT
    product,
    category,
    revenue
    FROM (
      SELECT
      product,
      category,
      revenue,
      dense_rank() OVER (PARTITION BY category ORDER BY revenue DESC) as rank
      FROM productRevenue) tmp
    WHERE
    rank <= 2
    """
    sqlContext.sql(sql).collect.foreach(println)
  }

  def window() {
    val sql = """
    SELECT
    product,
    category,
    revenue
    FROM (
      SELECT
      product,
      category,
      revenue,
      count(revenue) OVER (PARTITION BY product, category ORDER BY revenue DESC RANGE BETWEEN CURRENT ROW AND 1000 FOLLOWING) as total  FROM productRevenue where revenue > 4000 ) tmp
    WHERE
    total > 1
    """
    sqlContext.sql(sql).collect.foreach(println)
  }




  val sparkConf = new SparkConf().setAppName("SqlNetworkWordCount").setMaster("local[4]")
  val sc = new SparkContext(sparkConf)
  //    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)

  val df = sqlContext.read.json("./productRevenue.json")
  df.registerTempTable("productRevenue")
  test()
  Thread.sleep(10000)

}

