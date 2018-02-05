
import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types.StructType

object MyApp {
  def ncTesting(spark: SparkSession) = {
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
    query
  }

  def csvAppendTesting(spark: SparkSession) = {
    import spark.implicits._

    val userSchema = new StructType().add("name", "string").add("age", "integer")
    val csvDF = spark
      .readStream
      .option("sep", ",")
      .schema(userSchema) // Specify schema of the csv files
      .csv("./csv")
      .where("name = 'Binzi'")
    csvDF.printSchema

    val query = csvDF.writeStream
      .outputMode("append")
      .format("json")
      .option("checkpointLocation", "checkpoint")
      .option("path", "json")
      .start()

    query
  }

  def csvTesting(spark: SparkSession) = {
    import spark.implicits._

    val userSchema = new StructType().add("name", "string").add("age", "integer")
    val csvDF = spark
      .readStream
      .option("sep", ",")
      .schema(userSchema) // Specify schema of the csv files
      .csv("./csv")
      .groupBy("name")
      .agg(count("*").as("Total"))

    val query = csvDF.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    query
  }

  def main(args: Array[String]) {

    // Create the context with a 1 second batch size
    val spark = SparkSession
      .builder
      .master("local")
      .appName("MyStructStreamingApp")
      .getOrCreate()
    val query = csvTesting(spark)

    query.awaitTermination()
  }
}
