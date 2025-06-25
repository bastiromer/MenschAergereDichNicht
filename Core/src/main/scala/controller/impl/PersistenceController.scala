package controller.impl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import controller.PersistenceControllerInterface
import model.GameField
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*
import util.{handleResponse, sendHttpRequest}

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class PersistenceController extends PersistenceControllerInterface:
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "PersistenceController")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  override def getTargets: Future[List[String]] =
    val request = HttpRequest(uri = "http://persistence-service:8081/persistence/getTargets")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[List[String]])
    }

  override def load(fileName: String): Future[GameField] =
    val request = HttpRequest(uri = s"http://persistence-service:8081/persistence/load?file=$fileName")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[GameField])
    }

  override def save(gameField: GameField, fileName: String): Future[Unit] =
    val jsonBody = Json.toJson(gameField).toString()
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://persistence-service:8081/persistence/save?file=$fileName",
      entity = HttpEntity(ContentTypes.`application/json`, jsonBody)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }
