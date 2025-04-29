package controller.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controller.api.module.CoreModule.given_ControllerInterface
import controller.api.routes.CoreRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object CoreHttpServer:
  private[server] implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt("localhost", 8082)
      .bind(routes(CoreRoutes(given_ControllerInterface)))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Core Service -- Http Server is running at 8082\n")
      case Failure(exception) => logger.error(s"Core Service -- Http Server failed to start", exception)
    }
    serverBinding

  private def routes(coreRoutes: CoreRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("core") {
          concat(
            coreRoutes.coreRoutes
          )
        }
      )
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Core Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }