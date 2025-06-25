package gatling

import io.gatling.core.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.javaapi.core.PopulationBuilder
import io.gatling.jdbc.Predef.*

import scala.concurrent.duration.*

class ControllerLoadTest extends SimulationTemplate {
  override val operations: List[ChainBuilder] = List(
    buildOperation("API root", "GET", "/", StringBody("")),
  )

  override def executeOperations(): Unit = {
    val scn = buildScenario("Scenario 1")

    setUp(
      scn.inject(
        rampUsers(10) during (10.seconds)
      )

    ).protocols(httpProtocol)
  }

  executeOperations()
}