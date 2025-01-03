import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"
lazy val root = (project in file("."))
  .settings(
    name := "spark-streaming",
    scalaVersion := "2.12.18",
    libraryDependencies ++= Seq(
      // Spark dependencies
      "org.apache.spark" % "spark-core_2.12" % "3.4.1",
      "org.apache.spark" %% "spark-sql" % "3.4.1",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.3",
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.3",
      "org.apache.spark" %% "spark-mllib" % "3.4.1",

      // Kafka dependencies
      "org.apache.kafka" %% "kafka" % "3.5.1",
      "org.apache.kafka" % "kafka-clients" % "3.5.1",

      // Spark NLP for sentiment analysis
      "com.johnsnowlabs.nlp" %% "spark-nlp" % "5.5.1",

      // Elasticsearch
      "org.elasticsearch" %% "elasticsearch-spark-30" % "8.16.0",

      // Dot env
      "io.github.cdimascio" % "java-dotenv" % "5.2.2"

    )
  )
