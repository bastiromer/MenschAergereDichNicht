package controller.api.service


import controller.api.client.ModelClient

import model.GameField
import play.api.libs.json.{JsObject, Json}
import util.json.JsonWriters.given
import util.json.JsonReaders.given

import scala.concurrent.duration.*
import scala.concurrent.Await

object ModelRequestHttp:

  def save(gameField: GameField, filename: String): Unit =
    val jsonBody: JsObject = Json.obj("gameField" -> Json.toJson(gameField))
    val endpoint = s"/save/$filename"
    Await.result(ModelClient.postRequest(endpoint, jsonBody), 5.seconds)

  def load(filename: String): GameField =
    val jsonString = Await.result(ModelClient.getRequest(s"/load/$filename"), 5.seconds)
    Json.parse(jsonString).as[GameField]

  def getTargets(): List[String] =
    val jsonString = Await.result(ModelClient.getRequest("/targets"), 5.seconds)
    Json.parse(jsonString).as[List[String]]
