package tui.api.service

import model.modelComponents.{GameField, Move}
import tui.api.client.CoreClient
import model.modelComponents.json.JsonWriters.given
import model.modelComponents.json.JsonReaders.given

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

object CoreRequestHttp:
  
  def getGameField: GameField = {
    Await.result(CoreClient.getRequest("api/core/getGameField").map { json =>
      Json.parse(json).as[GameField]
    }, 5.seconds)
  }

  def undo(): Future[String] =
    CoreClient.postRequest("api/core/undo", Json.obj())
  
  def redo(): Future[String] =
    CoreClient.postRequest("api/core/redo", Json.obj())
  
  def dice(): Future[String] =
    CoreClient.postRequest("api/core/dice", Json.obj())
  
  def possibleMoves(): Future[List[Move]] =
    CoreClient.getRequest("api/core/possibleMoves").map { json =>
      Json.parse(json).as[List[Move]]
    }
  
  def makeMove(move: Move): Future[String] = {
    val jsonMove = Json.toJson(move)
    CoreClient.postRequest("api/core/makeMove", Json.obj(
      "move" -> jsonMove
    ))
  }

  def getTargets: Future[List[String]] =
    CoreClient.getRequest("api/core/getTargets").map { json =>
      Json.parse(json).as[List[String]]
    }
  
  def save(target: String): Future[String] =
    CoreClient.postRequest("api/core/save", Json.obj(
      "target" -> target
    ))
  
  def load(source: String): Future[String] =
    CoreClient.postRequest("api/core/load", Json.obj(
      "source" -> source
    ))

  def registerTUIObserver(tuiObserverUrl: String): Future[String] =
    CoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> tuiObserverUrl
    ))

  def deregisterTUIObserver(tuiObserverUrl: String): Future[String] =
    CoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> tuiObserverUrl
    ))
