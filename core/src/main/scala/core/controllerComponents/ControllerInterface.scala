package core.controllerComponents

import core.controllerComponents.util.Observable
import model.modelComponents.{GameField, Move}

import scala.util.Try

trait ControllerInterface extends Observable:
  def getGameField: GameField
  def possibleMoves: Try[List[Move]]
  def makeMove(move: Move): Try[Unit]
  def undo(): Try[Unit]
  def redo(): Try[Unit]
  def dice(): Try[Unit]
  def save(target: String): Try[Unit]
  def getTargets: Try[List[String]]
  def load(source: String): Try[Unit]

