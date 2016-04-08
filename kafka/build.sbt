name := "SparkStreaming"


scalaVersion := "2.10.5"

libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.9.0.1"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.4"
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.1.4"
libraryDependencies += "com.gensler" %% "scalavro" % "0.6.2"
libraryDependencies += "commons-codec" % "commons-codec" % "1.10"


addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
