package gatling

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object GatlingRunner {
  def main(args: Array[String]): Unit = {
    val simulations = List(
      classOf[ControllerLoadTest].getName,
      classOf[ControllerSpikeTest].getName,
      classOf[ControllerStressTest].getName,
      classOf[ControllerVolumeTest].getName
    )

    simulations.foreach { simClass =>
      val props = new GatlingPropertiesBuilder
      props.simulationClass(simClass)

      println(s"Running $simClass...")
      Gatling.fromMap(props.build)
    }
  }
}