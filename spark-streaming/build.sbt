import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"
lazy val root = (project in file("."))
  .settings(
    name := "spark-streaming",
    scalaVersion := "2.12.18",
    libraryDependencies ++= Seq(
      // Spark dependencies
      "org.apache.spark" % "spark-core_2.12" % "3.5.3",
      "org.apache.spark" %% "spark-sql" % "3.5.3",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.3",
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.3",
      "org.apache.spark" %% "spark-mllib" % "3.5.3",

      // Kafka dependencies
      "org.apache.kafka" %% "kafka" % "3.5.1",
      "org.apache.kafka" % "kafka-clients" % "3.5.1",

      // Spark NLP for sentiment analysis
      "com.johnsnowlabs.nlp" %% "spark-nlp-silicon" % "5.5.1"
    )
  )
