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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.Schema;
import kafka.utils.VerifiableProperties
import kafka.serializer.Encoder
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.Serializer
class AvroSerializer(props: VerifiableProperties = null) extends Serializer[String] {

  override def serialize(topic: String, json: String): Array[Byte] = {
    val schema = new Schema.Parser().parse(new File("src/main/resources/test_schema.avsc"));
    val reader = new GenericDatumReader[GenericRecord](schema);
    val input = new ByteArrayInputStream(json.getBytes());
    val output = new ByteArrayOutputStream();
    val din = new DataInputStream(input);
    val writer = new GenericDatumWriter[GenericRecord](schema);
    val decoder = DecoderFactory.get().jsonDecoder(schema, din);
    val encoder = EncoderFactory.get().binaryEncoder(output, null);
    Try{
    while (true) {
      val datum = reader.read(null, decoder)
      println(s"data = ${datum}")
      println("sssssddd")
      writer.write(datum, encoder);
    }
  }
    encoder.flush();
    println(s"encoding ${output.toByteArray}")
    return output.toByteArray();

  }
  override def close() = Unit
  override def configure(configs: Map[String, _], isKey: Boolean) = Unit

}

object AvroEncoderApp extends App {

  def send_avro = {
    import org.apache.kafka.clients.producer.ProducerConfig;
    import org.apache.kafka.clients.producer.KafkaProducer;
    import org.apache.kafka.clients.producer.ProducerRecord;

    val props = new Properties();
    props.put("bootstrap.servers", "127.0.0.1:9092");
    // props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    //props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    //   props.put("value.serializer", "AvroSerializer")

    //props.put("request.required.acks", "1");
    //val producer = new KafkaProducer[String, String](props)
    val producer = new KafkaProducer[String, String](props, new StringSerializer, new AvroSerializer)
    val json =
      """
    {
      "name":"hehe",
      "favorite_number":111,
      "favorite_color":"Red"
    }
    """
    println("sssss")

    val message = new ProducerRecord[String, String]("test", "sss", json);

    producer.send(message);
    producer.close
  }
  send_avro
}

