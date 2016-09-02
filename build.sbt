name := "panda-stats"

version := "1.0"

scalaVersion := "2.11.8"

mainClass in (Compile, run) := Some("com.panda.Boot")

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.9"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.9"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.9"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.9"

libraryDependencies += "commons-io" % "commons-io" % "2.5"
