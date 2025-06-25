package util.json

import model.*
import model.Player.Red
import play.api.libs.json.*

object JsonReaders:
  implicit val gameFieldReads: Reads[GameField] = new Reads[GameField]:
    def reads(json: JsValue): JsResult[GameField] =
      for {
        list <- (json \ "map").validate[List[((Int, Int), Cell)]]
        gameState <- (json \ "gameState").validate[GameState]
      } yield GameField(list.toMap, gameState)

  implicit val mapReads: Reads[List[((Int, Int), Cell)]] = new Reads[List[((Int, Int), Cell)]]:
    def reads(json: JsValue): JsResult[List[((Int, Int), Cell)]] =
      json.validate[List[JsObject]].map { list =>
        list.map { obj =>
          val positionObj = (obj \ "position").as[JsObject]
          val cell = (obj \ "cell").as[Cell]
          val x = (positionObj \ "x").as[Int]
          val y = (positionObj \ "y").as[Int]
          ((x, y), cell)
        }
      }

  implicit val gameStateReads: Reads[GameState] = new Reads[GameState]:
    def reads(json: JsValue): JsResult[GameState] =
      for {
        shouldDice <- (json \ "shouldDice").validate[Boolean]
        diceNumber <- (json \ "diceNumber").validate[Int]
        currentPlayer <- (json \ "currentPlayer").validate[String]
      } yield GameState(shouldDice, diceNumber, Player.fromString(currentPlayer))
  
  implicit val tokenReads: Reads[Token] = new Reads[Token]:
    def reads(json: JsValue): JsResult[Token] =
      for {
        player <- (json \ "player").validate[String]
        number <- (json \ "number").validate[Int]
      } yield Token(Player.fromString(player), number)

  implicit val cellReads: Reads[Cell] = new Reads[Cell]:
    def reads(json: JsValue): JsResult[Cell] =
      for {
        isAPlayField <- (json \ "isAPlayField").validate[Boolean]
        index <- (json \ "index").validate[Int]
        token <- (json \ "token").validateOpt[Token]
      } yield Cell(isAPlayField, index, token)

  implicit val moveReads: Reads[Move] = new Reads[Move]:
    def reads(json: JsValue): JsResult[Move] =
      for {
        toIndex <- (json \ "toIndex").validate[Int]
        fromIndex <- (json \ "fromIndex").validate[Int]
      } yield Move(toIndex, fromIndex)