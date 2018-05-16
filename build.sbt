name := """scala_bank"""

organization := "com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayService, RoutesCompiler)
  .settings(
    scalaVersion := "2.12.4",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      guice, // remove if not using Play's Guice loader
      akkaHttpServer, // or use nettyServer for Netty
      logback // add Play logging support
    )
  )

libraryDependencies += "org.joda" % "joda-convert" % "1.9.2"
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "1.1.1"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
