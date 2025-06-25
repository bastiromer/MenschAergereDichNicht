import com.typesafe.sbt.packager.Keys.{dockerBaseImage, dockerExposedPorts}
import sbt.librarymanagement.InclExclRule

val scala3Version = "3.1.2"
val scalacticVersion = "3.2.17"
val scalatestVersion = "3.2.17"
val scalaSwingVersion = "3.0.0"
val playJsonVersion = "2.10.4"
val akkaHttpVersion = "10.5.0"
val akkaActorVersion = "2.8.0"
val slickVersion = "3.5.0-M3"
val postgresqlVersion = "42.5.4"
val mongoVersion = "4.8.0"

ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "3.1.2"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / fork := true

updateOptions := updateOptions.value.withCachedResolution(false)

lazy val root = project
  .in(file("."))
  .dependsOn(model, tools, persistence, core, ui)
  .settings(
    name := "ToyBrokersLudo",
    commonSettings,
    coverage,
  )
  .aggregate(ui, core, model, persistence, tools)
  .enablePlugins(JacocoCoverallsPlugin)

lazy val ui = project
  .in(file("UI"))
  .dependsOn(model, tools)
  .settings(
    name := "UI",
    version:="0.1.0-SNAPSHOT",
    dockerBaseImage := "adoptopenjdk:11-jre-hotspot",
    dockerExposedPorts := Seq(8090),
    commonSettings,
  ).enablePlugins(JavaAppPackaging)

lazy val core = project
  .in(file("Core"))
  .dependsOn(model, tools)
  .settings(
    name := "Core",
    version:="0.1.0-SNAPSHOT",
    dockerBaseImage := "adoptopenjdk:11-jre-hotspot",
    dockerExposedPorts := Seq(8082),
    commonSettings,
  ).enablePlugins(JavaAppPackaging)

lazy val persistence = project
  .in(file("Persistence"))
  .dependsOn(model, tools)
  .settings(
    name := "Persistence",
    version:="0.1.0-SNAPSHOT",
    dockerBaseImage := "adoptopenjdk:11-jre-hotspot",
    dockerExposedPorts := Seq(8081),
    commonSettings,
  ).enablePlugins(JavaAppPackaging)

lazy val tools = project
  .in(file("Tools"))
  .dependsOn(model)
  .settings(
    name := "Tools",
    commonSettings
  )

lazy val model = project
  .in(file("Model"))
  .settings(
    name := "Model",
    commonSettings
  )

val gatlingExclude = Seq(("com.typesafe.akka", "akka-actor_2.13"), ("org.scala-lang.modules", "scala-java8-compat_2.13"), ("com.typesafe.akka","akka-slf4j_2.13")).toVector.map((org_name: Tuple2[String,String]) => InclExclRule(org_name._1, org_name._2))

lazy val commonSettings: Seq[Def.Setting[?]] = Seq(
  scalaVersion := scala3Version,
  javacOptions ++= Seq("-encoding", "UTF-8"),
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % scalacticVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.scala-lang.modules" %% "scala-swing" % scalaSwingVersion cross CrossVersion.for3Use2_13,
    "com.typesafe.play" %% "play-json" % playJsonVersion cross CrossVersion.for3Use2_13,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaActorVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaActorVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaActorVersion,
    "com.typesafe.slick" %% "slick" % slickVersion cross CrossVersion.for3Use2_13,
    "org.postgresql" % "postgresql" % postgresqlVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion cross CrossVersion.for3Use2_13,
    ("io.gatling.highcharts" % "gatling-charts-highcharts" % "3.9.5" % "test").withExclusions(gatlingExclude),
    ("io.gatling" % "gatling-test-framework" % "3.9.5" % "test").withExclusions(gatlingExclude)
  ))

lazy val coverage: Seq[Def.Setting[?]] = Seq(
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
    "utf-8"
  ),
  jacocoExcludes := Seq(
    "de.htwg.se.mill.Mill*",
    "de.htwg.se.mill.util*"
  ),
  jacocoCoverallsServiceName := "github-actions",
  jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
  jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
  jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
)
