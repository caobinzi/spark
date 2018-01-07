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
  def scalaPerson = {
    val p = Person(name = "s".some, age = 2.some)
    val r: String = JsonFormat.toJsonString(p)
    println(r)
    println(p.toByteArray.toList)
  }
  def javaPerson = {
    import com.example.tutorial.AddressBookProtos.AddressBook;
    import java.io.FileOutputStream;
    import com.example.tutorial.AddressBookProtos.Person;
    val output = new FileOutputStream("a.data")
    val john =
      Person.newBuilder()
        .setId(1234)
        .setName("John Doe")
        .setEmail("jdoe@example.com")
        .addPhones(
          Person.PhoneNumber.newBuilder()
            .setNumber("555-4321")
            .setType(Person.PhoneType.HOME)
        )
        .build();
    john.writeTo(output)
  }

  def main(args: Array[String]): Unit = {
    scalaPerson
    javaPerson
  }
}
