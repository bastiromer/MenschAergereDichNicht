package gatling

import scala.concurrent.duration._
import io.gatling.core.Predef.*
import io.gatling.http.Predef.*
import io.gatling.jdbc.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.javaapi.core.PopulationBuilder

class ControllerStressTest extends SimulationTemplate {
  override val operations: List[ChainBuilder] = List(
    buildOperation("API root", "GET", "/", StringBody("")),
    buildOperation("GameField", "GET", "/core/gameField", StringBody("")),
  )

  override def executeOperations(): Unit = {
    val scn = buildScenario("Scenario 1")

    setUp(
      scn.inject(
        stressPeakUsers(10000) during (20.second)
      )
    ).protocols(httpProtocol)
  }

  executeOperations()
}
