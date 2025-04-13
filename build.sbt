ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"

lazy val root = (project in file("."))
  .settings(
    name := "MenschAergerDichNicht",
    libraryDependencies ++= Seq(
      "org.playframework" %% "play-json" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalactic" %% "scalactic" % "3.2.17"
    )
  )
