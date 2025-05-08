package model.modelComponents

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class GameFieldSpec extends AnyWordSpec:
  "A GameField" should {
    "move" should {
      "move a token from one cell to another" in {
        val field = GameField.init()
        val from = 0
        val to = 28

        val move = Move(from, to)
        val newField = field.move(move)

        newField.map.values.find(_.index == from).get.token shouldBe None
        newField.map.values.find(_.index == to).get.token.isDefined shouldBe true
      }

      "switch player after move when dice != 6" in {
        val gameState = GameState(shouldDice = false, diceNumber = 4, currentPlayer = Player.Green)
        val map = GameField.init().map

        val fromIndex = 0
        val toIndex = 28
        val updatedMap = map.map {
          case (coord, cell) if cell.index == fromIndex =>
            coord -> cell.copy(token = Some(Token(Player.Green, 1)))
          case (coord, cell) if cell.index == toIndex =>
            coord -> cell.copy(token = None)
          case other => other
        }

        val field = GameField(updatedMap, gameState)
        val newField = field.move(Move(fromIndex, toIndex))

        newField.gameState.currentPlayer shouldBe Player.Red
      }
    }

    "rollDice" should {
      "correctly roll the dice and update state" in {
        val field = GameField.init()
        val newField = field.rollDice

        newField.gameState.diceNumber should (be >= 1 and be <= 6)
      }
    }

    "toString" should {
      "toString GameField" in {
        val fieldMap = Map(
          (0, 0) -> Cell(true, 0, None), (1, 0) -> Cell(true, 1, None),
          (0, 1) -> Cell(true, 2, None), (1, 1) -> Cell(true, 3, None)
        )
        val mockGameState = new GameState(false, 3, Player.Red) {
          override def toString: String = "Dice: 3\nRed have to move"
        }
        val gameField = new GameField(fieldMap, mockGameState)

        gameField.toString shouldBe " O  O \n O  O \nDice: 3\nRed have to move"
      }
    }
  }
