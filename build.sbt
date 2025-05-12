ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"


lazy val settings = Seq(
  libraryDependencies ++= Seq(
    "org.playframework" %% "play-json" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.scalactic" %% "scalactic" % "3.2.19",
    "com.typesafe.akka" %% "akka-http" % "10.5.3",
    "com.typesafe.akka" %% "akka-stream" % "2.8.8",
    "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8",
    "ch.qos.logback" % "logback-classic" % "1.5.18",
    "org.playframework" %% "play-json" % "3.0.4",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.8" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.5.3" % Test,
    "org.mockito" % "mockito-core" % "5.17.0" % Test,
  )
)


lazy val root = (project in file("."))
  .aggregate(model, persistence, core, tui)
  .dependsOn(model, persistence, core, tui)
  .enablePlugins(ScoverageSbtPlugin)
  .settings(
    name := "MenschAergerDichNicht",
    settings
  )

lazy val core = (project in file("core"))
  .dependsOn(model, persistence, persistence % "test->test")
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
  .dependsOn(model)
  .settings(
    name := "persistence",
    settings
  )


lazy val tui = (project in file("tui"))
  .dependsOn(model, persistence, core)
  .settings(
    name := "tui",
    settings
  )

