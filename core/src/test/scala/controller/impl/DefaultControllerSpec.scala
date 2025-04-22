package controller.impl

import fileIO.FileIOStub
import model.modelComponents.{GameField, Move, Player}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}


class DefaultControllerSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  var fileIO: FileIOStub = FileIOStub()
  var sut: DefaultController = DefaultController(using fileIO)

  override def beforeEach(): Unit = {
    fileIO = FileIOStub()
    sut = DefaultController(using fileIO)
  }

  "The Controller" should {

    "return possible moves when shouldDice is false" in {
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField

      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false greenPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(0, Player.Green.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false redPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(4, Player.Red.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false bluePlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(12, Player.Blue.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, currentPlayer = Player.Blue))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false yellowPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(12, Player.Yellow.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false red goOverEnd" in {
      var gameField = GameField.init()
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(5, 58))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, currentPlayer = Player.Red))
      sut.gameField = gameField
      println(sut.possibleMoves)
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return failure when shouldDice is true in possibleMoves" in {
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true))
      sut.gameField = gameField

      sut.possibleMoves shouldBe a[Failure[_]]
    }

    "make a move successfully" in {
      val move = Move(0, 20)
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField

      sut.makeMove(move) shouldBe Success(())
    }

    "make a move with should dice" in {
      val move = Move(0, 20)
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true))
      sut.gameField = gameField

      sut.makeMove(move) shouldBe a[Failure[_]]
    }

    "return failure when shouldDice is false in dice" in {
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      sut.gameField = gameField

      sut.dice() shouldBe a[Failure[_]]
    }

    "dice successfully" in {
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true))
      sut.gameField = gameField

      sut.dice() shouldBe Success(())
      sut.gameField.gameState.diceNumber should (be >= 1 and be <= 6)
    }

    "undo move successfully" in {
      val move = Move(0, 20)
      val gameField = GameField.init().copy(gameState = sut.gameField.gameState.copy(shouldDice = false))
      sut.gameField = gameField
      sut.makeMove(move) shouldBe Success(())

      sut.undo() shouldBe Success(())
    }

    "redo move successfully" in {
      val move = Move(0, 20)
      val gameField = GameField.init().copy(gameState = sut.gameField.gameState.copy(shouldDice = false))
      sut.gameField = gameField
      sut.makeMove(move) shouldBe Success(())
      sut.undo() shouldBe Success(())

      sut.redo() shouldBe Success(())
    }

    "undo dice successfully" in {
      sut.dice()

      sut.undo() shouldBe Success(())
    }

    "redo dice successfully" in {
      sut.dice()
      sut.undo() shouldBe Success(())

      sut.redo() shouldBe Success(())
    }


    "save successfully" in {
      val target = "test.txt"
      sut.save(target) shouldBe Success(())
      fileIO.saveCalls.last shouldBe((sut.gameField, target))
    }

    "get targets successfully" in {
      fileIO.getTargetsResult = List("example")
      sut.getTargets shouldBe Success(List("example"))
    }

    "load successfully" in {
      val source = "test.txt"
      sut.load(source) shouldBe Success(())
      fileIO.loadCalls.last shouldBe("test.txt")
    }


    "getGameFiled return gameField" in {
      sut.getGameField.toString shouldEqual sut.getGameField.toString
    }

    "gameField init should return a valid gameField" in {
      val expected = "G1G2OOOR1R2OOOG3G4OOOR3R4OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOY1Y2OOOB1B2OOOY3Y4OOOB3B4Dice:0Greenhavetodice"

      val actual = GameField.init().toString

      actual.replaceAll("\\s+", "") shouldEqual expected.replaceAll("\\s+", "")
    }

    "return possible moves when shouldDice is false 2" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(12, 13))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "return possible moves when shouldDice is false 3" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      gameField = gameField.move(Move(0, 58))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 4))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }

    "make a move to anther player" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      gameField = gameField.move(Move(0, 58))
      gameField = gameField.move(Move(4, 54))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 4, currentPlayer = Player.Red))
      sut.gameField = gameField
      sut.makeMove(Move(54, 58)) shouldBe Success(())
    }

    "move in end fields" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      gameField = gameField.move(Move(4, 74))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 4, currentPlayer = Player.Red))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(model.possibleMoves(gameField))
    }
  }}
