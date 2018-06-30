import java.io._

import io.protostuff.runtime.RuntimeSchema
import io.protostuff.ProtostuffIOUtil
import io.protostuff.{LinkedBuffer, Schema}
import model.{Message, Pojo}
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._



object Main {

  def java_unpack(inputStream: InputStream): scala.Iterator[Message] = {
    val in = new ObjectInputStream(inputStream)

    val iterator = new scala.Iterator[Message] {
      var o: AnyRef = null;

      override def hasNext: Boolean = {
        o = in.readObject()
        o != null
      }

      override def next(): Message = {
        o.asInstanceOf[Message]
      }
    }

    iterator
  }

  def protostuff_unpack(inputStream: InputStream): scala.Iterator[Message] = {

    val buffer: LinkedBuffer = LinkedBuffer.allocate(2048)
    val schema: Schema[Message] = RuntimeSchema.createFrom[Message](classOf[Message])

    val iterator = new scala.Iterator[Message] {
      var o: AnyRef = null;

      override def hasNext: Boolean = {
        val tmp = schema.newMessage()
        try {
          ProtostuffIOUtil.mergeDelimitedFrom[Message](inputStream, tmp, schema, buffer)
          o = tmp
        } finally {
          buffer.clear()
        }

        o != null
      }

      override def next(): Message = {
        o.asInstanceOf[Message]
      }
    }

    iterator
  }

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[8]").setAppName("NetworkWordCount")
    //conf.set("spark.streaming.blockInterval", "50")
    conf.set("spark.streaming.receiver.maxRate", "200000")

    //disable logging
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    def java_deserialize = (msg: Message) =>  {
      val serializer = new ObjectInputStream(new ByteArrayInputStream(msg.payload))
      serializer.readObject().asInstanceOf[Pojo]
    }

    def protostuff_deserialize = (msg: Message) => {
      // what is the cost on creating the schema many times
      val schema: Schema[Pojo] = RuntimeSchema.createFrom[Pojo](classOf[Pojo])

      val tmp = schema.newMessage()
      ProtostuffIOUtil.mergeFrom[Pojo](msg.payload, tmp, schema)

      tmp
    }

    val ssc = new StreamingContext(conf, Seconds(2))
    val sqlContext = new org.apache.spark.sql.SQLContext(ssc.sparkContext)

    // TODO change first deserialation for a byte read.
    val lines = ssc.socketStream("localhost", 8080, java_unpack, StorageLevel.MEMORY_ONLY);

    //lines.map( m => protostuff_deserialize(m) ).foreachRDD(rdd => rdd.foreach(p => println(p)))

    // this way the deserialization has concurrence
    lines.map( m => java_deserialize(m) ).map(p => p.map.get("check")).reduce((a,b) => a + b).print()

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate

  }
}
