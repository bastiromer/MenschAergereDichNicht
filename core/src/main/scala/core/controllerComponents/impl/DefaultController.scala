package core.controllerComponents.impl

import core.api.service.{ModelRequestHttp, PersistenceRequestHttp}
import core.controllerComponents.ControllerInterface
import core.controllerComponents.util.UndoManager
import fileIO.fileIOComponents.FileIO
import model.modelComponents.{GameField, Move, Token}
import model.modelComponents.*

import java.nio.file.NoSuchFileException
import scala.util.{Failure, Success, Try}
import concurrent.ExecutionContext.Implicits.global

class DefaultController(using fileIO: FileIO) extends ControllerInterface:
  var gameField: GameField = ModelRequestHttp.gameFieldInit()
  private val  undoManager = UndoManager[GameField]

  override def getGameField: GameField = gameField

  def possibleMoves: Try[List[Move]] = Try {
    if (gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Dice")
    } else {
      ModelRequestHttp.possibleMoves(gameField)
    }
  }

  override def makeMove(move: Move): Try[Unit] = Try {
    if (gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Dice")
    } else {
      gameField = undoManager.doStep(gameField, MoveCommander(generateValidMoveList(move), gameField.gameState))
      notifyObservers()
    }
  }

  override def dice(): Try[Unit] = Try {
    if (!gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Move")
    } else {
      gameField = undoManager.doStep(gameField, DiceCommander(gameField.gameState))
      notifyObservers()
    }
  }

  override def undo(): Try[Unit] = Try {
    gameField = undoManager.undoStep(gameField)
    notifyObservers()
  }

  override def redo(): Try[Unit] = Try {
    gameField = undoManager.redoStep(gameField)
    notifyObservers()
  }

  override def save(target: String): Try[Unit] = Try {
    PersistenceRequestHttp.save(gameField, target)
  }

  override def getTargets: Try[List[String]] = Try {
    PersistenceRequestHttp.getTargets
  }

  override def load(source: String): Try[Unit] = Try {
    PersistenceRequestHttp.load(source).onComplete {
      case Success(gameField) =>
        this.gameField = gameField
        undoManager.clear()
        notifyObservers()
      case Failure(exception) =>
        throw NoSuchFileException("Cant load target " + source + " from fileIO")
    }
  }

  private[impl] def generateValidMoveList(move: Move): List[Move] = {
    ModelRequestHttp.toCell(gameField, move).token match
      case Some(token: Token) => List(Move(
        fromIndex = move.toIndex,
        toIndex = gameField.map.values.find {
          cell => cell.index == token.playerHouseIndex
        }.get.index), move)
      case None => List(move)
  }


