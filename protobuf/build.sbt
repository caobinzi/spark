name := "ProtoBuf"
version := "0.1.1"


scalaVersion := "2.11.5"

libraryDependencies ++= Seq (
    "org.scalaz" %% "scalaz-core" % "7.2.0",
    "org.scalaz" %% "scalaz-concurrent" % "7.2.0",
    "org.apache.spark" % "spark-core_2.11" % "2.1.0" % "provided",
    "org.apache.spark" % "spark-hive_2.11" % "2.1.0" % "provided"
  )
libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-json4s" % "0.3.2"
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
mainClass in assembly := Some("RDDApp")
test in assembly := {}

