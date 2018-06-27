import java.io.{BufferedInputStream, InputStream, ObjectInputStream}

import model.Pojo
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._

//import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3

object Main {

  def converter(inputStream: InputStream): scala.Iterator[Pojo] = {
    val in = new ObjectInputStream(new BufferedInputStream(inputStream))

    val iterator = new scala.Iterator[Pojo] {
      var o: AnyRef = null;

      override def hasNext: Boolean = {
        o = in.readObject()
        o != null
      }

      override def next(): Pojo = {
        o.asInstanceOf[Pojo]
      }
    }

    iterator
  }

  def main(args: Array[String]): Unit = {


    val conf = new SparkConf().setMaster("local[4]").setAppName("NetworkWordCount")
    conf.set("spark.streaming.blockInterval", "50")
    conf.set("spark.streaming.receiver.maxRate", "200000")

    //disable logging
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val ssc = new StreamingContext(conf, Seconds(2))
    val sqlContext = new org.apache.spark.sql.SQLContext(ssc.sparkContext)

    val lines = ssc.socketStream("localhost", 8080, converter, StorageLevel.MEMORY_ONLY);

    lines.foreachRDD(rdd => println(" ----> " + rdd.count()))

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }
}
