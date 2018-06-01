name := """scala_bank"""

organization := "com"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, PlayService, RoutesCompiler)
  .settings(
    scalaVersion := "2.12.4",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      guice,
      akkaHttpServer,
      logback,
      "org.joda" % "joda-convert" % "1.9.2",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
      "io.lemonlabs" %% "scala-uri" % "1.1.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    )
  )

