organization := "akka_test"

name := "akka_test"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository" at "http://repo.spray.io",
  "Nightly Spray Repository" at "http://nightlies.spray.io"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "io.spray" % "spray-can" % "1.2-20130710",
  "io.spray" % "spray-routing" % "1.2-20130710",
  "io.spray" % "spray-util" % "1.2-20130710",
  "io.spray" % "spray-http" % "1.2-20130710"
)
