name := "ClusterEnvTester"

version := "1.0"

scalaVersion := "2.11.8"

val jacksonV = "2.4.1"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.3"
libraryDependencies += "com.typesafe.akka" % "akka-cluster_2.11" % "2.4.3"
libraryDependencies += "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4.3"
libraryDependencies += "joda-time"         %  "joda-time" %   "2.7"
libraryDependencies += "com.fasterxml.jackson.core"       %   "jackson-core"             %   jacksonV force()
libraryDependencies += "com.fasterxml.jackson.core"       %   "jackson-annotations"      %   jacksonV force()
libraryDependencies += "com.fasterxml.jackson.core"       %   "jackson-databind"         %   jacksonV force()
libraryDependencies += "com.fasterxml.jackson.dataformat" %   "jackson-dataformat-yaml"  %   jacksonV
libraryDependencies += "com.fasterxml.jackson.datatype"   %   "jackson-datatype-joda"    %   jacksonV
libraryDependencies += "org.reactivemongo" % "reactivemongo_2.11" % "0.11.11"
libraryDependencies += "com.typesafe.akka" % "akka-distributed-data-experimental_2.11" % "2.4.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.21"
