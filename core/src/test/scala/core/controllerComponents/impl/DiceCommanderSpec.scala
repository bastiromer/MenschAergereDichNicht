package core.controllerComponents.impl

import model.modelComponents.{GameField, GameState, Player}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class DiceCommanderSpec extends AnyWordSpec:
  var gameState: GameState = GameState(shouldDice = true, diceNumber = 2, currentPlayer = Player.Green)
  var commander = DiceCommander(gameState)
  val gameField: GameField = GameField.init()
  var newGameField: GameField = gameField
  
  "DiceCommander" should {
    "doStep" should  {
      "make a step" in {
        commander.doStep(newGameField) should not be gameField
      }
    }

    "undoStep" should {
      "undo the last step" in {
        commander.undoStep(newGameField) shouldBe gameField
      }
    }

    "redoStep" should {
      "redo the last undo" in {
        commander.redoStep(newGameField) shouldBe gameField
      }
    }
  }