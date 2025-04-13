package menschaergerdichnicht.fileIO

import menschaergerdichnicht.model.GameField
import scala.concurrent.Future

trait FileIO:
  def save(gameField: GameField, target: String): Unit
  def load(source: String): Future[GameField]
  def getTargets: List[String]


