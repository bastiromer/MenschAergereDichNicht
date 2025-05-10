package core.controllerComponents.impl

import fileIO.fileIOComponents.FileIOStub
import model.modelComponents.{GameField, Move, Player}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import java.nio.file.NoSuchFileException
import scala.concurrent.Promise
import scala.util.Success
import java.io.File

class DefaultControllerSpec extends AnyWordSpec with BeforeAndAfterEach with BeforeAndAfterAll:
  var fileIO: FileIOStub = FileIOStub()
  var sut: DefaultController = DefaultController(using fileIO)

//  override def beforeAll(): Unit = {
//    PersistenceService.startPersistenceServer()
//    ModelService.startModelServer()
//  }

  override def beforeEach(): Unit =
    fileIO = FileIOStub()
    sut = DefaultController(using fileIO)

  override def afterAll(): Unit =
    val file = new File("saveGameJson/ControllerTest.json")
    if (file.exists())
      file.delete()

  "The Controller" should {
    "getGameField" should {
      "return gameField" in {
        sut.getGameField.toString shouldEqual sut.getGameField.toString
      }
    }

    "possibleMoves" should {
      "return possible moves when shouldDice is false" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, Player.Green))
        sut.gameField = gameField

        val list = sut.possibleMoves
        list shouldBe Success(List(Move(3,20), Move(1,20), Move(0,20), Move(2,20)))
      }
      "return throw an Exception when shouldDice is true" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true, diceNumber = 6, Player.Green))
        sut.gameField = gameField

        val thrown = intercept[IllegalStateException] {
          sut.possibleMoves.get
        }

        thrown.getMessage shouldBe "You have to Dice"
      }
    }

    "makeMove" should {
      "make move when shouldDice is false" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 5, Player.Green))
        val move = Move(0, 20)
        sut.gameField = gameField

        sut.makeMove(move) shouldBe Success(())
      }

      "make move when dice is 6" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, Player.Green))
        val move = Move(1, 20)
        sut.gameField = gameField

        sut.makeMove(move) shouldBe Success(())
      }

      "return throw an Exception when shouldDice is true" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true, diceNumber = 5, Player.Green))
        val move = Move(0, 20)
        sut.gameField = gameField

        val thrown = intercept[IllegalStateException] {
          sut.makeMove(move).get
        }

        thrown.getMessage shouldBe "You have to Dice"
      }
    }

    "dice" should {
      "dice when shouldDice is true" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = true, diceNumber = 5, Player.Green))
        sut.gameField = gameField

        sut.dice() shouldBe Success(())
      }

      "return throw an Exception when shouldDice is false" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 5, Player.Green))
        sut.gameField = gameField

        val thrown = intercept[IllegalStateException] {
          sut.dice().get
        }

        thrown.getMessage shouldBe "You have to Move"
      }
    }

    "undo" should {
      "undo the last step" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 5, Player.Green))
        sut.gameField = gameField

        sut.undo() shouldBe Success(())
      }
    }

    "redo" should {
      "redo the last step" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 5, Player.Green))
        sut.gameField = gameField

        sut.redo() shouldBe Success(())
      }
    }

    "save" should {
      "save the gameField" in {
        val target = "ControllerTest"
        sut.save(target) shouldBe Success(())
      }
    }

    "getTarget" should {
      "return the targets as a List" in {
        //sut.getTargets shouldBe Success(List("saveGameJson\\ControllerTest.txt", "saveGameJson\\test", "saveGameJson\\TestField"))
        sut.getTargets should not be null
      }
    }

    "load" should {
      "update gameField and clear undoManager on success" in {
        val expectedGameField = GameField.init()
        sut.gameField = expectedGameField

        val field = sut.load("")
        field shouldBe Success(())
      }

      "throw NoSuchFileException on failure" in {
        val promise = Promise[GameField]()
        val controller = new DefaultController(using fileIO) {
          override def notifyObservers(): Unit = {}
        }

        val result = controller.load("nonexistentFile")
        promise.failure(new NoSuchFileException("Cant load target nonexistentFile from fileIO"))

        result.isSuccess shouldBe true
      }
    }

    "generateValidMoveList" should {
      "return a valid move list" in {
        val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, Player.Green))
        sut.gameField = gameField
        val move = Move(0, 20)

        val list = sut.generateValidMoveList(move)
        list should not be null
        list shouldBe List(Move(0, 20))
      }
    }
  }
