ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "spark-streaming",
    libraryDependencies ++= Seq(
      // Kafka dependencies
      "org.apache.kafka" %% "kafka" % "3.5.1",
      "org.apache.kafka" % "kafka-clients" % "3.5.1",
      "io.spray" %% "spray-json" % "1.3.6"
    )
  )
