name := "MyApp"
version := "0.1.1"
scalaVersion := "2.11.5"

libraryDependencies ++= Seq (
    "org.apache.spark" % "spark-core_2.11" % "2.2.0" % "provided",
    "org.apache.spark" % "spark-hive_2.11" % "2.2.0" % "provided"
  )
libraryDependencies += "com.databricks" %% "spark-avro" % "4.0.0"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")

mainClass in assembly := Some("MyApp")
test in assembly := {}

(run in Compile ) := 
   Defaults.runTask(
     fullClasspath in Compile, 
     mainClass in (Compile, run), 
     runner in (Compile, run)
   ).evaluated
