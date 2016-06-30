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

object KafakaDStreamTransformApp extends App {

  val sparkConf = new SparkConf().setAppName("SqlNetworkWordCount").setMaster("local[4]")
  val sparkContext = new SparkContext(sparkConf)
  val ssc = new StreamingContext(sparkContext, Seconds(2))
  val kafkaParams = Map[String, String]("metadata.broker.list" -> "127.0.0.1:9092")
  val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, AvroDecoder](ssc, kafkaParams, Set("test"))

   @transient private var instance: SQLContext = _
   def getSQLContext(sc: SparkContext): SQLContext = {
     if (instance == null) {
       instance = new org.apache.spark.sql.hive.HiveContext(sc)
     }
     instance
   }
def myTransform(rdd:RDD[String]):RDD[String] =  {
  val sqlContext = getSQLContext(sparkContext)
  sqlContext.read.json(rdd).toJSON
}

  messages.map(_._2).transform(myTransform _ ).print

  // Start the computation
  ssc.start()
  ssc.awaitTermination()
  Thread.sleep(1000000)

}

