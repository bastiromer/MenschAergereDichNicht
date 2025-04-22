package model.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model.api.routes.ModelRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ModelHttpServer:
  private[server] implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt("localhost", 8081) //MODEL_HOST, MODEL_PORT)
      .bind(routes(new ModelRoutes))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Model Service -- Http Server is running at 8081\n")  //$MODEL_BASE_URL
      case Failure(exception) => logger.error(s"Model Service -- Http Server failed to start", exception)
    }
    serverBinding

  private def routes(fieldRoutes: ModelRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("model") {
          concat(
            pathPrefix("field") {
              concat(
                fieldRoutes.modelRoutes
              )
            }
          )
        }
      )
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Model Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }