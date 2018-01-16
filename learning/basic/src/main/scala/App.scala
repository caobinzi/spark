import org.apache.spark.sql._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scalaz._
import Scalaz._
case class People(age: BigInt, id: BigInt, name: String)

object MyApp {
  def getDataFrameFromJson(
    spark:    SparkSession,
    jsonFile: String
  ): DataFrame = {
    spark.read.json(jsonFile)
  }
  def getSparkSession(
    appName: String,
    master:  Option[String]
  ): SparkSession = {
    val spark = SparkSession.builder
    master.cata(
      some = spark.master,
      none = spark
    ).getOrCreate
  }
  def main(args: Array[String]): Unit = {
    val master = args.headOption
    val spark = getSparkSession("MyApp", master)
    val dataFrame = getDataFrameFromJson(spark, "people.json")
    import spark.implicits._
    dataFrame.show
    dataFrame.as[People].show
    Thread.sleep(100000)
  }
}
