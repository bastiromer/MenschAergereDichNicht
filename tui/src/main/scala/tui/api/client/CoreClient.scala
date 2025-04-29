package tui.api.client

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.StreamTcpException
import java.net.ConnectException
import org.slf4j.LoggerFactory
import play.api.libs.json.JsObject
import scala.concurrent.{ExecutionContext, Future}

object CoreClient:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val ec: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)
  private val http = Http(system)

  private final val CORE_BASE_URL = "http://localhost:8082/"

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-core-client") { () =>
    shutdown
  }

  def getRequest(endpoint: String): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = CORE_BASE_URL.concat(endpoint)
      )
    )

  def postRequest(endpoint: String, json: JsObject): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = CORE_BASE_URL.concat(endpoint),
        entity = HttpEntity(ContentTypes.`application/json`, json.toString)
      )
    )

  private def sendRequest(request: HttpRequest): Future[String] =
    http.singleRequest(request).flatMap { response =>
      response.status match
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { body =>
            val errorMsg = s"HTTP ERROR: ${response.status} - ${request.uri} - $body"
            logger.error(errorMsg)
            Future.failed(new RuntimeException(errorMsg))
          }
    }
    /*
    http
      .singleRequest(request)
      .flatMap { response =>
        Unmarshal(response.entity).to[String].map { body =>
          response.status match
            case StatusCodes.OK        => Right(body)
            case StatusCodes.Forbidden => Left(body)
            case _ =>
              val errorMsg = s"HTTP ERROR: ${response.status} - ${request.uri} - $body"
              logger.error(errorMsg)
              throw new RuntimeException(errorMsg)
        }
      }
      .recoverWith {
        case exception: StreamTcpException if exception.getCause.isInstanceOf[ConnectException] =>
          val msg = s"Connection error: Unable to reach ${request.uri}"
          if (request.uri.toString.endsWith("/deregisterObserver")) then
            val warnMsg = msg.concat(" (ignored for deregisterObserver) Probably the Core Server was shut down before.")
            logger.warn(warnMsg)
            Future.successful(Right(warnMsg))
          else
            logger.error(msg)
            Future.failed(new RuntimeException(msg))
        case exception =>
          logger.error(s"Unexpected error: ${exception.getMessage}", exception)
          Future.failed(exception)
      }
    */

  private def shutdown: Future[Done] =
    logger.info("TUI Service -- Shutting Down Core Client...")
    http.shutdownAllConnectionPools().map(_ => Done)
