import org.apache.spark.sql._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scalaz._
import Scalaz._
import org.apache.spark.sql.functions._
class TaskError {
  override def toString = "I will cause an error"
}

object MyApp {
  def getDataFrameFromJson(
    spark:    SparkSession,
    jsonFile: String
  ): DataFrame = {

    val dataFrame = spark.read.json(jsonFile)
    val nameLength = udf((name: String) => name.length)
    dataFrame.withColumn("NameLength", nameLength(col("name")))

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

    val taskError = new TaskError
    dataFrame.foreach{
      d =>
        //println(taskError) //This will cause task serialization error
        val error = new TaskError
        println(error)
    }
    dataFrame.show

    Thread.sleep(100000) //Sleep
  }
}
