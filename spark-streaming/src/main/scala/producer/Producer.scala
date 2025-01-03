import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties
import scala.io.Source

object Producer {

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    // Creating Kafka Producer
    val producer = new KafkaProducer[String, String](props)
    val topic = "tweets"
    var tweetCount = 0

    val filePath = "data/boulder_flood_geolocated_tweets.json"

    try {
      // Reading the file line by line
      val lines = Source.fromFile(filePath).getLines()

      // Send each line directly as a Kafka message
      for (line <- lines) {
        try {
          val record = new ProducerRecord[String, String](topic, s"key${tweetCount}", line)
          producer.send(record)
          println(s"Sent raw JSON to Kafka topic: $topic")
          tweetCount += 1
          // Delay sending tweets to simulate a real-time tweet generator (60 tweets per second)
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
      producer.close()
     }
  }}