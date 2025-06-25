package gatling

import io.gatling.core.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.jdbc.Predef.*
import model.GameField
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*

import scala.concurrent.duration.*

class PersistenceVolumeTest extends SimulationTemplate {
  
  val gameFieldJson: String = Json.toJson(GameField.init()).toString()

  override val operations: List[ChainBuilder] = List(
    buildOperation("API root", "GET", "/", StringBody("")),
    buildOperation("Save GameField", "POST", "/persistence/save?file=testfile", StringBody(gameFieldJson)),
    buildOperation("Load GameField", "GET", "/persistence/load?file=testfile", StringBody("")),
    buildOperation("Get Targets", "GET", "/persistence/getTargets", StringBody("")),
    buildOperation("Database Save", "POST", "/persistence/databaseSave", StringBody(gameFieldJson)),
    buildOperation("Database Load", "GET", "/persistence/databaseLoad", StringBody("")),
    buildOperation("Database Update", "POST", "/persistence/databaseUpdate", StringBody(gameFieldJson)),
    buildOperation("Database Delete", "POST", "/persistence/databaseDelete", StringBody(""))
  )

  override def executeOperations(): Unit = {
    val scn = buildScenario("Scenario 1")
    val scn2 = buildScenario("Scenario 2")
    val scn3 = buildScenario("Scenario 3")

    setUp(
      scn.inject(
        rampUsersPerSec(10) to 100 during (10.second)
      ).andThen(
        scn2.inject(
          constantUsersPerSec(100) during (10.second)
        )
      ).andThen(
        scn3.inject(
          rampUsersPerSec(100) to 0 during (10.second)
        )
      )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
