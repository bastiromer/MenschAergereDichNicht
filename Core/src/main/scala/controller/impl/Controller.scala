package controller.impl

import controller.PersistenceControllerInterface
import controller.impl.PersistenceController
import model.*
import model.Player.{Blue, Green, Red, Yellow}
import util.{Observable, UndoManager}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class Controller(using persistenceController: PersistenceControllerInterface) extends Observable: 
  
  var gameField: GameField = GameField.init()
  private val undoManager = UndoManager[GameField]

  def getGameField: GameField = gameField

  def possibleMoves: Try[List[Move]] = Try {
    if (gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Dice")
    } else {
      gameField.possibleMoves()
    }
  }

  def makeMove(move: Move): Try[Unit] = Try {
    if (gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Dice")
    } else {
      gameField = undoManager.doStep(gameField, MoveCommander(generateValidMoveList(move), gameField.gameState))
      notifyObservers()
    }
  }

  def dice(): Try[Unit] = Try {
    if (!gameField.gameState.shouldDice) {
      throw new IllegalStateException("You have to Move")
    } else {
      gameField = undoManager.doStep(gameField, DiceCommander(gameField.gameState))
      notifyObservers()
    }
  }

  def undo(): Try[Unit] = Try {
    gameField = undoManager.undoStep(gameField)
    notifyObservers()
  }
  
  def redo(): Try[Unit] = Try {
    gameField = undoManager.redoStep(gameField)
    notifyObservers()
  }

  def save(target: String): Future[Unit] = 
    persistenceController.save(gameField, target)
  
  
  def getTargets: Future[List[String]] = 
    persistenceController.getTargets


  def load(source: String): Future[Unit] =
    persistenceController.load(source).map { loadedGameField =>
      gameField = loadedGameField
      undoManager.clear()
      notifyObservers()
    }
  
  private def generateValidMoveList(move: Move): List[Move] =
    move.toCell(gameField.map).token match
      case Some(token: Token) => List(Move(
        fromIndex = move.toIndex,
        toIndex = gameField.map.values.find {
          cell => cell.index == token.playerHouseIndex
        }.get.index), move)
      case None => List(move)

