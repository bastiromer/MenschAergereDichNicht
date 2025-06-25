package controller.impl

import model.{GameField, GameState, Player}
import util.Command

import scala.util.Random

class DiceCommander(var gameState: GameState) extends Command[GameField]:
  override def doStep(gameField: GameField): GameField =
     gameState = gameField.gameState
     gameField.rollDice

  override def undoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    GameField(gameField.map, oldGameState)

  override def redoStep(gameField: GameField): GameField =
    val oldGameState = gameState
    gameState = gameField.gameState
    GameField(gameField.map, oldGameState)