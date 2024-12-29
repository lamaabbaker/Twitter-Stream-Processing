import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties
import scala.io.Source

object producer {

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, "1048576") // 1 MB, adjust as needed
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "10485760") // 10 MB, adjust as needed

    // Creating Kafka Producer
    val producer = new KafkaProducer[String, String](props)
    val topic = "tweets"

    // Path to the file of data
    val filePath = "./data/boulder_flood_geolocated_tweets.json"

    try {
      // Reading the file line by line
      val lines = Source.fromFile(filePath).getLines()

      // Send each line directly as a Kafka message
      for (line <- lines) {
        try {
          // Send the raw JSON line to Kafka
          val record = new ProducerRecord[String, String](topic, null, line)
          producer.send(record)
          println(s"Sent raw JSON to Kafka topic: $topic")

          Thread.sleep(16)
        } catch {
          case e: Exception =>
            println(s"Error sending line: $line")
            e.printStackTrace()
        }
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      // Closing the producer
      producer.close()
    }
}}