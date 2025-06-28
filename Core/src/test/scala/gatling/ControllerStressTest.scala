package gatling

import io.gatling.core.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.jdbc.Predef.*

import scala.concurrent.duration.*

class ControllerStressTest extends SimulationTemplate {
  override val operations: List[ChainBuilder] = List(
    buildOperation("API root", "GET", "/", StringBody("")),
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
