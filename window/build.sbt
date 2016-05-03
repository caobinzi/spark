name := "SparkStreaming"


scalaVersion := "2.10.5"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.4"
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.1.4"
libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-hive_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.6.1"
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
