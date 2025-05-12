package model.modelComponents

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class PossibleMovesSpec extends AnyWordSpec:
  "possibleMoves" should {
    "return correct moves for a token on the board with legal dice roll" in {
      val player = Player.Yellow
      val token = Token(player, 1)
      val fromIndex = 21
      val dice = 4
      val toIndex = 25
      val cellWithToken = Cell(isAPlayField = true, index = fromIndex, token = Some(token))
      val emptyToCell = Cell(isAPlayField = true, index = toIndex, token = None)
      val gameFieldMap = Map(
        (0, 0) -> cellWithToken,
        (1, 0) -> emptyToCell
      )
      val gameState = GameState(
        shouldDice = false,
        diceNumber = dice,
        currentPlayer = player
      )
      val gameField = GameField(gameFieldMap, gameState)
      val moves = gameField.possibleMoves()

      moves should have size 1
      moves.head shouldBe Move(fromIndex = fromIndex, toIndex = toIndex)
    }

    "return empty list if no legal moves are available" in {
      val player = Player.Yellow
      val token = Token(player, 1)
      val dice = 3
      val blockedCell = Cell(isAPlayField = true, index = 21, token = Some(token))
      val occupiedByOther = Cell(isAPlayField = true, index = 24, token = Some(Token(Player.Green, 2)))
      val gameFieldMap = Map(
        (0, 0) -> blockedCell,
        (1, 0) -> occupiedByOther
      )
      val gameState = GameState(
        shouldDice = false,
        diceNumber = dice,
        currentPlayer = player
      )
      val gameField = GameField(gameFieldMap, gameState)

      gameField.possibleMoves() shouldBe empty
    }
  }


