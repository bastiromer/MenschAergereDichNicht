package model

import model.{Player, Token}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class TokenSpec extends AnyWordSpec {
  "A Token" when {
    "converted to string" should {
      "return the correct representation" in {
        val greenToken = Token(Player.Green, 1)
        greenToken.toString shouldEqual "G1"

        val redToken = Token(Player.Red, 2)
        redToken.toString shouldEqual "R2"

        val yellowToken = Token(Player.Yellow, 3)
        yellowToken.toString shouldEqual "Y3"

        val blueToken = Token(Player.Blue, 4)
        blueToken.toString shouldEqual "B4"
      }
    }

    "calculating playerHouseIndex" should {
      "return the correct index" in {
        val greenToken = Token(Player.Green, 1)
        greenToken.playerHouseIndex shouldEqual 0

        val redToken = Token(Player.Red, 2)
        redToken.playerHouseIndex shouldEqual 5

        val yellowToken = Token(Player.Yellow, 3)
        yellowToken.playerHouseIndex shouldEqual 10

        val blueToken = Token(Player.Blue, 4)
        blueToken.playerHouseIndex shouldEqual 15
      }
    }
  }
}

