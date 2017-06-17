// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.14")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.2.10")

// Scala formatting: "sbt scalafmt"
// https://olafurpg.github.io/scalafmt
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.3.1")

// DB
libraryDependencies += ("com.typesafe.slick" %% "slick" % "3.1.0")

// resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
