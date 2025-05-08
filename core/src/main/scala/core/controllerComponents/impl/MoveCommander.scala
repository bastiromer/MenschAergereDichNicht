package core.controllerComponents.impl

import core.api.service.ModelRequestHttp
import model.modelComponents.{GameField, GameState, Move}
import util.Command

class MoveCommander(moves: List[Move], var gameState: GameState) extends Command[GameField]:
  override def doStep(gameField: GameField): GameField =
    gameState = gameField.gameState
    moves.foldLeft(gameField)((currentGameField, move) =>
      ModelRequestHttp.move(currentGameField, move)
      //currentGameField.move(move)
    )

  override def undoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    moves.reverse.foldLeft(gameField)((currentGameField, move) =>
      ModelRequestHttp.move(currentGameField, Move(fromIndex = move.toIndex, toIndex = move.fromIndex))
      //currentGameField.move(Move(fromIndex = move.toIndex, toIndex = move.fromIndex))
    )

  override def redoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    moves.foldLeft(gameField)((currentGameField, move) =>
      ModelRequestHttp.move(currentGameField, move)
      //currentGameField.move(move)
    )
