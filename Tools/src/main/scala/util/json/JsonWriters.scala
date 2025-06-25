package util.json

import model.*
import play.api.libs.json.{JsValue, Json, Writes}

object JsonWriters:
  implicit val gameFieldWrites: Writes[GameField] = new Writes[GameField]:
    def writes(gameField: GameField): JsValue =
      Json.obj(
        "map" -> Json.toJson(gameField.map.toList),
        "gameState" -> Json.toJson(gameField.gameState)
      )

  implicit val mapWrites: Writes[List[((Int, Int), Cell)]] = new Writes[List[((Int, Int), Cell)]]:
    def writes(list: List[((Int, Int), Cell)]): JsValue =
      Json.toJson(list.map { case ((x, y), cell) =>
        Json.obj(
          "position" -> Json.obj("x" -> x, "y" -> y),
          "cell" -> Json.toJson(cell)
        )
      })

  implicit val gameStateWrites: Writes[GameState] = new Writes[GameState]:
    def writes(gameState: GameState): JsValue =
      Json.obj(
        "shouldDice" -> gameState.shouldDice,
        "diceNumber" -> gameState.diceNumber,
        "currentPlayer" -> gameState.currentPlayer.toString
      )

  implicit val cellWrites: Writes[Cell] = new Writes[Cell]:
    def writes(cell: Cell): JsValue =
      val tokenJson = cell.token.map(Json.toJson(_)).getOrElse(Json.obj())
      Json.obj(
        "isAPlayField" -> cell.isAPlayField,
        "index" -> cell.index,
        "token" -> cell.token
      )

  implicit val tokenWrites: Writes[Token] = new Writes[Token]:
    def writes(token: Token): JsValue =
      Json.obj(
        "player" -> token.toString,
        "number" -> token.number
      )

  implicit val moveWrites: Writes[Move] = new Writes[Move]:
    def writes(move: Move): JsValue =
      Json.obj(
        "toIndex" -> move.toIndex,
        "fromIndex" -> move.fromIndex
      )    

