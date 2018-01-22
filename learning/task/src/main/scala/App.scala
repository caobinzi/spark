import org.apache.spark.sql._
import org.apache.spark.sql.functions._




class TaskError {
  override def toString = "I will cause an error"
}


object MyApp {


  def logTime[R](info:String)(block: => R):  R = {
    println(s"${info} Start")
    val t0     = System.nanoTime()
    val result = block // call-by-name
    val t1     = System.nanoTime()
    val timeTaken = (t1 - t0) / 1000000
    println(s"${info} Done in ${timeTaken} milli seconds")
    result
  }

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
  master.map(
    spark.master
  ).getOrElse(spark)
.getOrCreate
  }



  def main(args: Array[String]): Unit = {
    val master = args.headOption
    val spark = getSparkSession("MyApp", master)
    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")

    val dataFrame = getDataFrameFromJson(spark, "people.json")

    dataFrame.cache
    val totalNum = dataFrame.where("id =1").count
    val records = dataFrame.groupBy("id")
    .agg(count("*").as("total"))
    .where("total > 2")
    .collect
    dataFrame.unpersist




    logTime("<<<Foreach>>>") {
      dataFrame.foreach{ 
      d =>
      println(s"I'm working on record ${d}")
    }



  }

  dataFrame.show

  Thread.sleep(100000) //Sleep
}
}
