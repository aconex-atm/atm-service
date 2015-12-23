enablePlugins(JavaServerAppPackaging)

mainClass in Compile := Some("com.example.unfilter.Server")

name := "atm-service"

version := "1.0"

scalaVersion := "2.11.7"


libraryDependencies += "net.databinder" %% "unfiltered-directives" % "0.8.3"

libraryDependencies += "net.databinder" %% "unfiltered-filter" % "0.8.3"

libraryDependencies += "net.databinder" %% "unfiltered-netty" % "0.8.3"

libraryDependencies += "net.databinder" %% "unfiltered-netty-websockets" % "0.8.3"

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.8.3"

libraryDependencies += "net.databinder" %% "unfiltered-netty-server" % "0.8.3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.7"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-sns" % "1.9.6"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "com.github.etaty" %% "rediscala" % "1.5.0"

