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
case class Person(id: String, name: String)
object RDDApp {
  def run(sc: SparkContext) = {
    while (true) {
      val r = scala.util.Random
      val data = (1 to r.nextInt(100)).toList.map { a =>
        Person(a.toString, a.toString)
      }
      val rdd = sc.parallelize(data)
      rdd.cache
      println("running")
      val a = (1 to 100).toList.map { x =>
        Future(rdd.filter(_.id == x.toString).collect)
      }
      a.foreach { f =>
        println(Await.ready(f, Duration.Inf).value.get)
      }
      rdd.unpersist()
    }

  }
  def main(args: Array[String]): Unit = {

    val master = args.headOption

    val conf =
      master.cata(
        some = new SparkConf().setAppName("test").setMaster(_),
        none = new SparkConf().setAppName("test")
      )
    val sc = new SparkContext(conf)
    run(sc)

  }
}
