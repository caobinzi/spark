import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.io.AvroTypeIO
import scala.util.{Try, Success, Failure}

case class Person(name: String, age: Int)
object Avro {
  val personType = AvroType[Person]
  def test = {
     personType.io writeJson Person("Jack", 12)
  }
}

