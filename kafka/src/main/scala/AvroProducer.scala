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

object AvroProducer extends App {

  def send_avro = {
    import org.apache.avro.Schema;
    import org.apache.avro.generic.GenericData;
    import org.apache.avro.generic.GenericRecord;
    import org.apache.avro.io._;
    import org.apache.avro.specific.SpecificDatumReader;
    import org.apache.avro.specific.SpecificDatumWriter;
    import org.apache.commons.codec.DecoderException;
    import org.apache.commons.codec.binary.Hex;
    import org.apache.kafka.clients.producer.ProducerConfig;
    import org.apache.kafka.clients.producer.KafkaProducer;
    import org.apache.kafka.clients.producer.ProducerRecord;

    val props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    props.put("request.required.acks", "1");
    val  producer = new KafkaProducer[String, Array[Byte]](props);

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

    val message = new ProducerRecord[String, Array[Byte]]("test", "sss", serializedBytes);

    producer.send(message);
    producer.close

  }
  send_avro
}


