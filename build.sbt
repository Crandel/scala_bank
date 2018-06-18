name := """scala_bank"""

organization := "com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayService, RoutesCompiler)
  .settings(
    scalaVersion := "2.12.6",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      guice,
      akkaHttpServer,
      logback,
      "org.joda" % "joda-convert" % "1.9.2",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
      "com.typesafe.akka" %% "akka-distributed-data" % "2.5.13",
      "org.abstractj.kalium" % "kalium" % "0.8.0",
      "io.lemonlabs" %% "scala-uri" % "1.1.1",
      "org.scalaj" %% "scalaj-http" % "2.4.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    )
  )

