name := "consumer"

version := "0.1"

scalaVersion := "2.11.0"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.3.0"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.3.0"
libraryDependencies += "io.protostuff" % "protostuff-runtime" % "1.5.9"
libraryDependencies += "io.protostuff" % "protostuff-core" % "1.5.9"