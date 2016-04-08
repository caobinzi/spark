import scala.language.higherKinds
import scala.language.implicitConversions
import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import concurrent._
import java.util._
import java.io.File
import java.io.ByteArrayOutputStream
import kafka.javaapi.producer.Producer
import kafka.producer.KeyedMessage
import kafka.producer.ProducerConfig

object StringProducer extends App {

  def send_producerecord = {
    import org.apache.kafka.clients._
    import org.apache.kafka.clients.producer._
    val props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", "0");
    props.put("batch.size", "16384");
    props.put("linger.ms", "1");
    props.put("buffer.memory", "33554432");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    val producer = new KafkaProducer[String, String](props);
    (1 to 100).foreach {
      x => 
      producer.send(new ProducerRecord[String, String]("test", s"${x}", s"a${x%10}"));
    }
    producer.close()
  }
  send_producerecord
}


