import controller.PersistenceControllerInterface
import controller.impl.Controller
import model.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Await
import concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class DefaultControllerSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  var persistenceControllerStub: PersistenceControllerStub = PersistenceControllerStub()
  var sut: Controller = Controller(using persistenceControllerStub)

  override def beforeEach(): Unit = {
    persistenceControllerStub = PersistenceControllerStub()
    sut = Controller(using persistenceControllerStub)
  }

  "The Controller" should {

    "return possible moves when shouldDice is false" in {
      val gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField

      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false greenPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(0, Player.Green.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false redPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(4, Player.Red.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false bluePlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(12, Player.Blue.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, currentPlayer = Player.Blue))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false yellowPlayerStart" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(12, Player.Yellow.firstCellIndex))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false red goOverEnd" in {
      var gameField = GameField.init()
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6))
      gameField = gameField.move(Move(5, 58))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 6, currentPlayer = Player.Red))
      sut.gameField = gameField
      println(sut.possibleMoves)
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
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
      Await.result(sut.save(target), 3.seconds)
      persistenceControllerStub.saveCalls.last shouldBe((sut.gameField, target))
    }

    "get targets successfully" in {
      persistenceControllerStub.getTargetsResult = List("example")
      Await.result(sut.getTargets, 3.seconds) shouldBe List("example")
    }

    "load successfully" in {
      val source = "test.txt"
      Await.result(sut.load(source), 3.seconds)
      persistenceControllerStub.loadCalls.last shouldBe("test.txt")
    }

    "getGameFiled return gameField" in {
      sut.getGameField.toString shouldEqual (sut.getGameField.toString)
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
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "return possible moves when shouldDice is false 3" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      gameField = gameField.move(Move(0, 58))
      gameField = gameField.copy(gameState = GameField.init().gameState.copy(shouldDice = false, diceNumber = 4))
      sut.gameField = gameField
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
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
      sut.possibleMoves shouldBe Success(gameField.possibleMoves())
    }

    "deleteGame" in {
      var gameField = GameField.init().copy(gameState = GameField.init().gameState.copy(shouldDice = false))
      gameField = gameField.move(Move(4, 74))
      sut.gameField = gameField
      sut.deleteGame()
      Thread.sleep(100)
      val expected = "G1G2OOOR1R2OOOG3G4OOOR3R4OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOY1Y2OOOB1B2OOOY3Y4OOOB3B4Dice:0Greenhavetodice"
      val actual = GameField.init().toString
    }
    
    "init controller should read from database" in {
      persistenceControllerStub.loadDatabaseCalls shouldBe 1
    }
    
    "dice should write to database" in {
      sut.dice()
      persistenceControllerStub.updateDatabaseCalls.length shouldBe 1
    }
  }}