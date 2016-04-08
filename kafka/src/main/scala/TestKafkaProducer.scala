import scala.language.higherKinds
import scala.language.implicitConversions
import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import concurrent._
import java.util._
import kafka.javaapi.producer.Producer
import kafka.producer.KeyedMessage
import kafka.producer.ProducerConfig

object TestKafkaProducer extends App {
  def send_key_message() = {
    val props = new Properties();

    props.put("metadata.broker.list", "localhost:9092");
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");
    val config = new ProducerConfig(props);

    val producer = new Producer[String, String](config);
    val rnd = new Random()

    val runtime = new Date().getTime();  
    val ip = "192.168.2." + rnd.nextInt(255); 
    val msg = runtime + ",www.example.com," + ip; 
    val data = new KeyedMessage[String, String]("test", ip, Avro.test.toString);
    producer.send(data)
    producer.close
  }

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
    producer.send(new ProducerRecord[String, String]("test", "a", "a"));
    producer.close()
  }
  send_producerecord
}


