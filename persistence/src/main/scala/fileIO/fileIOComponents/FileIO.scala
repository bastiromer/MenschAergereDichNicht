package fileIO.fileIOComponents

import model.modelComponents.GameField
import scala.concurrent.Future

trait FileIO:
  def save(gameField: GameField, target: String): Either[(String, String), String]
  def load(source: String): Future[GameField]
  def getTargets: List[String]


