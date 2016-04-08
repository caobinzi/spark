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
    (1 to 100).foreach {
      x => 
      producer.send(new ProducerRecord[String, String]("test", s"${x}", s"a${x%10}"));
    }
    producer.close()
  }
  def send_avro = {
    import org.apache.avro.Schema;
    import org.apache.avro.generic.GenericData;
    import org.apache.avro.generic.GenericRecord;
    import org.apache.avro.io._;
    import org.apache.avro.specific.SpecificDatumReader;
    import org.apache.avro.specific.SpecificDatumWriter;
    import org.apache.commons.codec.DecoderException;
    import org.apache.commons.codec.binary.Hex;

    val props = new Properties();
    props.put("metadata.broker.list", "localhost:9092");
    props.put("serializer.class", "kafka.serializer.DefaultEncoder");
    props.put("request.required.acks", "1");
    val config = new ProducerConfig(props);
    val  producer = new Producer[String, Array[Byte]](config);

    val schema = new Schema.Parser().parse(new File("src/main/resources/test_schema.avsc"));
    val payload1 = new GenericData.Record(schema);

    //Step2 : Put data in that genericrecord object
    payload1.put("name", "dbevent1");
    payload1.put("favorite_number", 111);
    payload1.put("favorite_color", "Red");
    System.out.println("Original Message : "+ payload1);

    //Step3 : Serialize the object to a bytearray
    val writer = new SpecificDatumWriter[GenericRecord](schema);
    val out = new ByteArrayOutputStream();
    val encoder = EncoderFactory.get().binaryEncoder(out, null);
    writer.write(payload1, encoder);
    encoder.flush();
    out.close();

    val serializedBytes = out.toByteArray();
    println("Sending message in bytes : " + serializedBytes);
    val serializedHex = Hex.encodeHexString(serializedBytes);
    System.out.println("Serialized Hex String : " + serializedHex);
    val  message = new KeyedMessage[String, Array[Byte]]("test", "page_views", serializedBytes);

    producer.send(message);
    producer.close

  }
  send_producerecord
}


