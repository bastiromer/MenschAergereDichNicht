package core.api.service

import core.api.client.PersistenceClient
import play.api.libs.json.Json

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import model.modelComponents.GameField
import model.modelComponents.json.JsonReaders.*
import model.modelComponents.json.JsonWriters.*

import scala.concurrent.ExecutionContext.Implicits.global

object PersistenceRequestHttp:

  def getTargets: List[String] =
    Await.result(PersistenceClient.getRequest("api/persistence/fileIO/getTargets").map { jsonString =>
      Json.parse(jsonString).as[List[String]]
    }, 5.seconds)
    
  def load(fileName: String): Future[GameField] =
    PersistenceClient.postRequest("api/persistence/fileIO/load", Json.obj(
      "fileName" -> fileName
    )).map { jsonString =>
      Json.parse(jsonString).as[GameField]
    }
    
  def save(gameField: GameField, fileName: String): String =
    val jsonField = Json.toJson(gameField)
    Await.result(PersistenceClient.postRequest("api/persistence/fileIO/save", Json.obj(
      "fileName" -> fileName,
      "gameField" -> jsonField
    )), 5.seconds)
