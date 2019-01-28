name := """scala_bank"""

organization := "com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayService, RoutesCompiler)
  .settings(
    scalaVersion := "2.12.8",
    ensimeScalaVersion in ThisBuild := scalaVersion.value,
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      guice,
      akkaHttpServer,
      logback,
      "com.pauldijou" %% "jwt-play-json" % "1.1.0",
      "com.typesafe.akka" %% "akka-distributed-data" % "2.5.19",
      "io.lemonlabs" %% "scala-uri" % "1.4.0",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.3",
      "org.joda" % "joda-convert" % "2.2.0",
      "org.scalaj" %% "scalaj-http" % "2.4.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    )
  )

