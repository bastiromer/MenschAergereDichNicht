package aview

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import model.{GameField, Move}
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*
import util.{Observable, handleResponse, sendHttpRequest}

import scala.concurrent.{ExecutionContextExecutor, Future}

class CoreController extends Observable:
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "CoreController")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  establishWebSocketConnection()

  def gameField: Future[GameField] =
    val request = HttpRequest(uri = "http://core-service:8082/core/gameField")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[GameField])
    }

  def possibleMoves: Future[List[Move]] =
    val request = HttpRequest(uri = "http://core-service:8082/core/possibleMoves")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[List[Move]])
    }

  def move(move: Move): Future[Unit] =
    val jsonBody = Json.toJson(move).toString()
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/move",
      entity = HttpEntity(ContentTypes.`application/json`, jsonBody)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def dice(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/dice"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def undo(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/undo"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def redo(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/redo"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def save(fileName: String): Future[Unit] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://core-service:8082/core/save",
      entity = HttpEntity(ContentTypes.`application/json`, fileName)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }
  }

  def load(fileName: String): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/load",
      entity = HttpEntity(ContentTypes.`application/json`, fileName)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }
    
  def newGame(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/newGame",
      entity = HttpEntity(ContentTypes.`application/json`, "")
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }
  
  def getTargets: Future[List[String]] =
    val request = HttpRequest(uri = "http://core-service:8082/core/getTargets")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[List[String]])
    }

  private def establishWebSocketConnection(): Future[Unit] = {
    val wsUrl = "ws://core-service:8082/core/changes"

    val (webSocketUpgradeResponse, webSocketOut) =
      Http().singleWebSocketRequest(
        WebSocketRequest(uri = wsUrl),
        Flow.fromSinkAndSourceMat(
          Sink.foreach[Message] {
            _ => notifyObservers()
          },
          Source.actorRef[TextMessage](bufferSize = 10, OverflowStrategy.fail)
            .mapMaterializedValue { webSocketIn =>
              system.log.info("WebSocket connected")
              webSocketIn
            }
        )(Keep.both)
      )

    webSocketUpgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        println("WebSocket connection established")
        Future.successful(())
      } else {
        println(s"WebSocket connection failed: ${upgrade.response.status}")
        throw new RuntimeException(s"WebSocket connection failed: ${upgrade.response.status}")
      }
    }
  }
