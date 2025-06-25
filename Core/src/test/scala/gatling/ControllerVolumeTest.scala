package gatling

import scala.concurrent.duration._
import io.gatling.core.Predef.*
import io.gatling.http.Predef.*
import io.gatling.jdbc.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.javaapi.core.PopulationBuilder

class ControllerVolumeTest extends SimulationTemplate {
  override val operations: List[ChainBuilder] = List(
    buildOperation("API root", "GET", "/", StringBody("")),
    buildOperation("GameField", "GET", "/core/gameField", StringBody("")),
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

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
