import scala.language.higherKinds
import scala.language.implicitConversions
import scala.concurrent.duration.Duration
import scala.util.{Try, Failure, Success}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.concurrent._
import ExecutionContext.Implicits.global
import scalaz._
import Scalaz._

object RDDApp {

  def getRdd(sc: SparkContext) = {
    val r = scala.util.Random
    val data = (1 to r.nextInt(20000))
    sc.parallelize(data)
  }

  def getSparkContext(master: Option[String]) = {
    val conf =
      master.cata(
        some = new SparkConf().setAppName("test").setMaster(_),
        none = new SparkConf().setAppName("test")
      )
    new SparkContext(conf)
  }

  def main(args: Array[String]): Unit = {

    val master = args.headOption
    val sc = getSparkContext(master)
    val rdd = getRdd(sc)
    rdd.foreach(println)
  }
}
