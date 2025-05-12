package core.controllerComponents.impl

import core.api.service.ModelRequestHttp
import core.controllerComponents.util.Command
import model.modelComponents.{GameField, GameState}

class DiceCommander(var gameState: GameState) extends Command[GameField]:
  override def doStep(gameField: GameField): GameField =
    gameState = gameField.gameState
    ModelRequestHttp.rollDice(gameField)

  override def undoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    GameField(gameField.map, oldGameState)

  override def redoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    GameField(gameField.map, oldGameState)
