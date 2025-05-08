package model.modelComponents

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class CellSpec extends AnyWordSpec:
  "A Cell" should {
    "return a string with token if a token is present" in {
      val player = Player.Red
      val token = Token(player, 1)
      val cell = Cell(isAPlayField = true, index = 0, token = Some(token))

      cell.toString shouldBe "R1 "
    }

    "return ' O ' if it is a play field and has no token" in {
      val cell = Cell(isAPlayField = true, index = 1, token = None)

      cell.toString shouldBe " O "
    }

    "return three spaces if it is not a play field and has no token" in {
      val cell = Cell(isAPlayField = false, index = 2, token = None)

      cell.toString shouldBe "   "
    }

    "store the correct index and playfield flag" in {
      val cell = Cell(isAPlayField = true, index = 42, token = None)

      cell.index shouldBe 42
      cell.isAPlayField shouldBe true
    }

    "store the correct token if given" in {
      val token = Token(Player.Yellow, 2)
      val cell = Cell(isAPlayField = true, index = 5, token = Some(token))

      cell.token shouldBe Some(token)
    }
  }