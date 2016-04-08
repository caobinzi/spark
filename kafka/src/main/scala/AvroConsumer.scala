import scala.language.higherKinds
import scala.language.implicitConversions
import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import concurrent._
import java.util._
import java.io.File
import java.io.ByteArrayOutputStream
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import scala.collection.JavaConversions._

object AvroReceiver extends App {

  def receive_avro = {
    import org.apache.avro.Schema;
    import org.apache.avro.generic.GenericData;
    import org.apache.avro.generic.GenericRecord;
    import org.apache.avro.io._;
    import org.apache.avro.specific.SpecificDatumReader;
    import org.apache.avro.specific.SpecificDatumWriter;
    import org.apache.commons.codec.DecoderException;
    import org.apache.commons.codec.binary.Hex;

    val props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");

    val consumer = new KafkaConsumer[String, Array[Byte]](props);
    consumer.subscribe(Collections.singletonList("test"));

    val schema = new Schema.Parser().parse(new File("src/main/resources/test_schema.avsc"));
    val payload1 = new GenericData.Record(schema);
    while (true) {
      val records = consumer.poll(1000).iterator().toList
      records.foreach {
        record =>
          val received_message = record.value();
          System.out.println(received_message);
          val schema = new Schema.Parser().parse(new File("src/main/resources/test_schema.avsc"));
          val reader = new SpecificDatumReader[GenericRecord](schema);
          val decoder = DecoderFactory.get().binaryDecoder(received_message, null);
          val payload2 = reader.read(null, decoder);
          System.out.println("Message received : " + payload2);
      }
      Thread.sleep(1000);
    }

  }
  receive_avro
}

