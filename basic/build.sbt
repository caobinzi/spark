name := "RDDApp"
version := "0.1.1"


scalaVersion := "2.11.5"

libraryDependencies ++= Seq (
    "org.scalaz" %% "scalaz-core" % "7.2.0",
    "org.scalaz" %% "scalaz-concurrent" % "7.2.0",
    "org.apache.spark" % "spark-core_2.11" % "2.2.0" % "provided",
    "org.apache.spark" % "spark-hive_2.11" % "2.2.0" % "provided"
  )

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
mainClass in assembly := Some("RDDApp")
test in assembly := {}
//run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
(run in Compile ) := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
