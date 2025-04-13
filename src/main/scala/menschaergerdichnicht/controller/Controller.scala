package menschaergerdichnicht.controller

import menschaergerdichnicht.model.{GameField, Move}
import menschaergerdichnicht.util.Observable
import scala.util.Try

trait Controller extends Observable:
  def getGameField: GameField
  def possibleMoves: Try[List[Move]]
  def makeMove(move: Move): Try[Unit]
  def undo(): Try[Unit]
  def redo(): Try[Unit]
  def dice(): Try[Unit]
  def save(target: String): Try[Unit]
  def getTargets: Try[List[String]]
  def load(source: String): Try[Unit]

