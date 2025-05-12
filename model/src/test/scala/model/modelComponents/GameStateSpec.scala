package model.modelComponents

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class GameStateSpec extends AnyWordSpec:
  "GameState" should {
    "rollDice" should {
      "change diceNumber to 4 and player on rollDice" in {
        val initial = new GameState(shouldDice = true, diceNumber = 0, currentPlayer = Player.Green) {
          override def dice: Int = 4
        }
        val field = GameField.init()
        val newState = initial.rollDice(field.map)

        newState.currentPlayer should not be Player.Green
      }

      "change diceNumber to 6 on rollDice" in {
        val initial = new GameState(shouldDice = true, diceNumber = 0, currentPlayer = Player.Green) {
          override def dice: Int = 6
        }
        val field = GameField.init()
        val newState = initial.rollDice(field.map)

        newState.currentPlayer shouldBe Player.Green
      }
    }

    "dice" should {
      "return a random number between 1 and 6" in {
        val state = GameState(shouldDice = true, diceNumber = 0, currentPlayer = Player.Green)

        val rolls = (1 to 1000).map(_ => state.dice)
        all(rolls) should (be >= 1 and be <= 6)
      }
    }

    "computeMove" should {
      "only allow current player to move" in {
        val state = GameState(shouldDice = false, diceNumber = 4, currentPlayer = Player.Red)
        val nextState = state.computeMove(Player.Blue)

        nextState shouldBe state
      }

      "dice again on 6" in {
        val state = GameState(shouldDice = false, diceNumber = 6, currentPlayer = Player.Yellow)
        val updated = state.computeMove(Player.Yellow)

        updated.currentPlayer shouldBe Player.Yellow
        updated.shouldDice shouldBe true
      }

      "trigger a new dice roll from a new Player if dice was not 6" in {
        val state = GameState(shouldDice = false, diceNumber = 3, currentPlayer = Player.Blue)
        val updated = state.computeMove(Player.Blue)

        updated.currentPlayer shouldBe Player.Yellow
      }
    }

    "toString" should {
      "toString have to move" in {
        val state = GameState(shouldDice = false, diceNumber = 6, currentPlayer = Player.Yellow)

        state.toString shouldBe "Dice: 6\nYellow have to move"
      }
      "toString have to dice" in {
        val state = GameState(shouldDice = true, diceNumber = 3, currentPlayer = Player.Yellow)

        state.toString shouldBe "Dice: 3\nYellow have to dice"
      }
    }
  }
