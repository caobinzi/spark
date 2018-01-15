import org.apache.spark.sql._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scalaz._
import Scalaz._

object RDDApp {
  def getDataFrame(
    spark: SparkSession,
    json:  String
  ): DataFrame = {
    spark.read.json(json)
  }
  def getSparkSession(master: Option[String]): SparkSession = {
    val spark = SparkSession.builder
      .appName("RDD App")
    master.cata(
      some = spark.master,
      none = spark
    ).getOrCreate
  }
  def main(args: Array[String]): Unit = {
    val master = args.headOption
    val json = "people.json"
    val spark = getSparkSession(master)
    val dataFrame = getDataFrame(spark, json)
    dataFrame.show
  }
}
