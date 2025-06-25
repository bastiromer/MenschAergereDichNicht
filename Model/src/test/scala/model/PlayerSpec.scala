package model

import model.Player
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec {
  "Player" when {
    "created from string" should {
      "return the correct player" in {
        Player.fromString("r") shouldEqual Player.Red
        Player.fromString("b") shouldEqual Player.Blue
        Player.fromString("y") shouldEqual Player.Yellow
        Player.fromString("g") shouldEqual Player.Green
      }

      "throw IllegalArgumentException for invalid input" in {
        an [IllegalArgumentException] should be thrownBy Player.fromString("x")
      }
    }

    "calculating next player" should {
      "return the correct next player" in {
        Player.Green.next shouldEqual Player.Red
        Player.Red.next shouldEqual Player.Blue
        Player.Blue.next shouldEqual Player.Yellow
        Player.Yellow.next shouldEqual Player.Green
      }
    }

    "calculating firstCellIndex" should {
      "return the correct index" in {
        Player.Green.firstCellIndex shouldEqual 20
        Player.Red.firstCellIndex shouldEqual 30
        Player.Yellow.firstCellIndex shouldEqual 50
        Player.Blue.firstCellIndex shouldEqual 40
      }
    }

    "calculating lastCellIndex" should {
      "return the correct index" in {
        Player.Green.lastCellIndex() shouldEqual 59
        Player.Red.lastCellIndex() shouldEqual 29
        Player.Yellow.lastCellIndex() shouldEqual 49
        Player.Blue.lastCellIndex() shouldEqual 39
      }
    }

    "calculating endCellIndexes" should {
      "return the correct list of indexes" in {
        Player.Green.endCellIndexes() shouldEqual List(70, 71, 72, 73)
        Player.Red.endCellIndexes() shouldEqual List(74, 75, 76, 77)
        Player.Yellow.endCellIndexes() shouldEqual List(82, 83, 84, 85)
        Player.Blue.endCellIndexes() shouldEqual List(78, 79, 80, 81)
      }
    }
  }
}

