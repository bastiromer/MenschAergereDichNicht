package core.controllerComponents.impl

import model.modelComponents.{GameField, GameState, Move, Player}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class MoveCommanderSpec extends AnyWordSpec:
  val move: Move = Move(fromIndex = 0, toIndex = Player.Green.firstCellIndex)
  val initialGameField: GameField = GameField.init()
  val preparedGameField: GameField = initialGameField.move(move)
  val gameState: GameState = preparedGameField.gameState

  val commander = MoveCommander(List(move), gameState)

  "MoveCommander" should {
    "doStep" should {
      "apply the move and change game field" in {
        val result = commander.doStep(initialGameField)
        result should not be initialGameField
        result.map.exists(_._2.token.nonEmpty) shouldBe true
      }
    }

    "undoStep" should {
      "undo the move and return to previous state" in {
        val afterMove = commander.doStep(initialGameField)
        val reverted = commander.undoStep(afterMove)

        reverted should not be afterMove
      }
    }

    "redoStep" should {
      "redo the move after undo" in {
        val afterMove = commander.doStep(initialGameField)
        val reverted = commander.undoStep(afterMove)
        val redone = commander.redoStep(reverted)
        
        redone should not be reverted
      }
    }
  }