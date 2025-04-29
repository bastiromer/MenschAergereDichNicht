package controller.api.service


import controller.api.client.ModelClient
import model.modelComponents.{Cell, GameField, Move}
import play.api.libs.json.Json
import model.modelComponents.json.JsonWriters.given
import model.modelComponents.json.JsonReaders.given

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

object ModelRequestHttp:

  def gameFieldInit(): GameField =
    Await.result(ModelClient.postRequest("api/model/field/gameFieldinit",Json.obj()).map { json =>
      Json.parse(json).as[GameField]
    }, 5.seconds)

  def possibleMoves(gameField: GameField): List[Move] =
    val jsonField = Json.toJson(gameField).toString()
    Await.result(ModelClient.postRequest("api/model/field/possibleMoves", Json.obj(
      "field" -> jsonField
    )).map { jsonString =>
      Json.parse(jsonString).as[List[Move]]
    }, 5.seconds)

  def toCell(gameField: GameField, move: Move): Cell =
    val jsonField = Json.toJson(gameField).toString()
    val jsonMove = Json.toJson(move).toString()
    Await.result(ModelClient.postRequest("api/model/field/toCell", Json.obj(
      "field" -> jsonField,
      "move" -> jsonMove
    )).map { jsonString =>
      Json.parse(jsonString).as[Cell]
    }, 5.seconds)

  def move(gameField: GameField, move: Move): GameField =
    val jsonField = Json.toJson(gameField).toString()
    val jsonMove = Json.toJson(move).toString()
    Await.result(ModelClient.postRequest("api/model/field/move", Json.obj(
      "field" -> jsonField,
      "move" -> jsonMove
    )).map { jsonString =>
      Json.parse(jsonString).as[GameField]
    }, 5.seconds)
    
  def rollDice(gameField: GameField): GameField =
    val jsonField = Json.toJson(gameField).toString()
    //gameField.rollDice
    Await.result(ModelClient.postRequest("api/model/field/rollDice", Json.obj(
      "field" -> jsonField
    )).map { jsonString =>
      Json.parse(jsonString).as[GameField]
    }, 5.seconds)