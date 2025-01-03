package consumer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import org.apache.spark.sql.streaming.Trigger
import io.github.cdimascio.dotenv.Dotenv



object Consumer {

  def main(args: Array[String]): Unit = {

    // Load environment variables for Elasticsearch
    val dotenv = Dotenv.load()
    val esUser = dotenv.get("ES_USER")
    val esPassword = dotenv.get("ES_PASSWORD")
    val esSslCert = dotenv.get("ES_SSL_CERT")

    val sparkConf = new SparkConf ()
    .setAppName ("TweetProcessor")
    .setMaster ("local[*]")


    val spark = SparkSession.builder
      .config(sparkConf)
      .config("spark.es.nodes", "localhost")
      .config("spark.es.port", "9200")
      .config("spark.es.net.http.auth.user", esUser) // Elasticsearch username
      .config("spark.es.net.http.auth.pass", esPassword) // Elasticsearch password
      .config("spark.es.net.ssl", "true") // Enable SSL/TLS if Elasticsearch is secured with HTTPS
      .config("spark.es.net.ssl.cert", esSslCert) // Path to your certificate
      .config("spark.es.nodes.wan.only", "true")
      .getOrCreate()

    import spark.implicits._

    val kafkaBootstrapServers = "localhost:9092"
    val kafkaTopic = "tweets"

    // Define the schema for tweets
    val tweetSchema = StructType (Seq (
    StructField ("created_at", StringType, nullable = true),
    StructField ("id", LongType, nullable = true),
    StructField ("id_str", StringType, nullable = true),
    StructField ("text", StringType, nullable = true),
    StructField ("truncated", BooleanType, nullable = true),
    StructField ("entities", StructType (Seq (
    StructField ("hashtags", ArrayType (StructType (Seq (
    StructField ("text", StringType, nullable = true)
    ) ) ), nullable = true)
    ) ), nullable = true),
    StructField ("geo", StructType (Seq (
    StructField ("coordinates", ArrayType (DoubleType), nullable = true)
    ) ), nullable = true),
    StructField ("place", StructType (Seq (
    StructField ("coordinates", ArrayType (ArrayType (ArrayType (DoubleType) ) ), nullable = true),
    StructField ("type", StringType, nullable = true)
    ) ), nullable = true),
    StructField ("coordinates", StructType (Seq (
    StructField ("type", StringType, nullable = true),
    StructField ("coordinates", ArrayType (DoubleType), nullable = true)
    ) ), nullable = true),
    StructField("lang", StringType, nullable = true),
    StructField("source", StringType, nullable = false),
    ) )


    val rawTweetsStream = spark.readStream
  .format ("kafka")
  .option ("kafka.bootstrap.servers", kafkaBootstrapServers)
  .option ("subscribe", kafkaTopic)
  .option ("startingOffsets", "latest")
  .load ()

    val tweets = rawTweetsStream
  .selectExpr ("CAST(value AS STRING) as json_value")
  .withColumn ("json_value", from_json ($"json_value", tweetSchema) )
  .select (
    $"json_value.text",
    $"json_value.id_str".alias ("tweet_id"),
    $"json_value.created_at",
    $"json_value.lang".alias("language"),
    $"json_value.coordinates.coordinates".alias ("geo_coordinates"),
    transform ($"json_value.entities.hashtags", h => h.getField ("text") ).alias ("hashtags"),
    expr("regexp_extract(json_value.source, '>(.*?)<', 1) as tweet_source"),
  )



    val sentimentPipeline = PretrainedPipeline ("analyze_sentiment", lang = "en")

    val analyzeSentiment = udf ((text: String) => {
    val annotation = sentimentPipeline.annotate (text)
    annotation ("sentiment").headOption match {
    case Some ("positive") => 1
    case Some ("negative") => - 1
    case _ => 0
  }
  })

    // Only run sentiment analysis on English text, for non-English text, assign a neutral sentiment
    val tweetsWithSentiment = tweets.withColumn("sentiment",
      when($"language" === "en", analyzeSentiment($"text"))
        .otherwise(lit(0))
    )


    // Write the processed data to Elasticsearch
    tweetsWithSentiment.writeStream
      .format ("org.elasticsearch.spark.sql")
      .option ("es.nodes", "localhost:9200")
      .option ("es.resource", "tweets")
      .option ("es.mapping.id", "tweet_id")
      .option ("checkpointLocation", "checkpoint")
      .trigger (Trigger.ProcessingTime ("10 seconds") )
      .start ()
      .awaitTermination ()

  }
}


