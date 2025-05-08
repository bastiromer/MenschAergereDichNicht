package model.modelComponents

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class GameStateSpec extends AnyWordSpec:
  "GameState" should {
    "rollDice" should {
      "change diceNumber and possibly player on rollDice" in {
        val initial = GameState(shouldDice = true, diceNumber = 0, currentPlayer = Player.Green)
        val field = GameField.init()
        val newState = initial.rollDice(field.map)

        newState.diceNumber should (be >= 1 and be <= 6)

        if (GameField(field.map, newState).possibleMoves().isEmpty)
          newState.currentPlayer should not be Player.Green
        else
          newState.currentPlayer shouldBe Player.Green
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
