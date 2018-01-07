import scalaz._
import Scalaz._
import scala.language.higherKinds
import scala.language.implicitConversions
import scala.concurrent.duration.Duration
import scala.util.{Try, Failure, Success}
import scala.concurrent._
import ExecutionContext.Implicits.global
import message._
import com.trueaccord.scalapb.json.JsonFormat
object ProtoBufApp {
  def main(args: Array[String]): Unit = {
    val p = Person(name = "s".some, age = 2.some)
    val r: String = JsonFormat.toJsonString(p)
    println(r)
  }
}
