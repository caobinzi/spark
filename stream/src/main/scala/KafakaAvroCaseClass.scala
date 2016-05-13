import scala.language.higherKinds
import scala.language.implicitConversions
import scala.util.Try
import scalaz._
import scalaz.Scalaz._
import concurrent._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.sql.DataFrame
import org.apache.spark.streaming.dstream.DStream
import kafka.serializer.StringDecoder

import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.avro.Schema;
import kafka.utils.VerifiableProperties

import kafka.serializer.Decoder
import org.apache.avro.Schema
import org.apache.avro.io.{BinaryDecoder, DecoderFactory, DatumReader}
import org.apache.avro.specific.{SpecificRecordBase, SpecificDatumReader}
import java.io.File
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.DataFileWriter;

class AvroCaseDecoder[T <: SpecificRecordBase](props: VerifiableProperties = null, schema: Schema)
    extends Decoder[T] {

  private[this] val NoBinaryDecoderReuse = null.asInstanceOf[BinaryDecoder]
  private[this] val NoRecordReuse = null.asInstanceOf[T]
  private[this] val reader: DatumReader[T] = new SpecificDatumReader[T](schema)

  override def fromBytes(bytes: Array[Byte]): T = {
    val decoder = DecoderFactory.get().binaryDecoder(bytes, NoBinaryDecoderReuse)
    reader.read(NoRecordReuse, decoder)
  }

}

object KafakaAvroCaseClassApp extends App {
  import example.avro.User
  class UserDecoder(props: VerifiableProperties = null) extends AvroCaseDecoder[User](
    props,
    User.SCHEMA$
  )

  val sparkConf = new SparkConf().setAppName("SqlNetworkWordCount").setMaster("local[4]")
  val ssc = new StreamingContext(sparkConf, Seconds(2))
  val kafkaParams = Map[String, String]("metadata.broker.list" -> "127.0.0.1:9092")

  val messages = KafkaUtils.createDirectStream[String, User, StringDecoder, UserDecoder](ssc, kafkaParams, Set("test"))

  messages.foreachRDD {
    rdd => rdd.foreach(println)
  }

  // Start the computation
  ssc.start()
  ssc.awaitTermination()
  Thread.sleep(1000000)

}

