ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"


lazy val settings = Seq(
  libraryDependencies ++= Seq(
    "org.playframework" %% "play-json" % "3.0.1",
    "org.scalatest" %% "scalatest" % "3.2.17" % Test,
    "org.scalactic" %% "scalactic" % "3.2.17",
    "com.typesafe.akka" %% "akka-http" % "10.5.3", //cross CrossVersion.for3Use2_13,
    "com.typesafe.akka" %% "akka-stream" % "2.8.8", //cross CrossVersion.for3Use2_13,
    "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8", //cross CrossVersion.for3Use2_13,
    "ch.qos.logback" % "logback-classic" % "1.5.18",
    "org.playframework" %% "play-json" % "3.0.4",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.8" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test
  )
)


lazy val root = (project in file("."))
  .aggregate(model, util, persistence, core, tui)
  .dependsOn(model, util, persistence, core, tui)
  .settings(
    name := "MenschAergerDichNicht",
    settings
  )

lazy val core = (project in file("core"))
  .dependsOn(model, util, persistence, persistence % "test->test")
  .settings(
    name := "core",
    settings
  )


lazy val model = (project in file("model"))
  .settings(
    name := "model",
    settings
  )

lazy val persistence = (project in file("persistence"))
  .dependsOn(model, util)
  .settings(
    name := "persistence",
    settings
  )


lazy val tui = (project in file("tui"))
  .dependsOn(model, util, persistence, core)
  .settings(
    name := "tui",
    settings
  )

lazy val util = (project in file("util"))
  .dependsOn(model)
  .settings(
    name := "util",
    settings
  )
