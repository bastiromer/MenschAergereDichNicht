package controller



import model.GameField

import scala.concurrent.Future

trait PersistenceControllerInterface:
  def getTargets: Future[List[String]]
  def load(fileName: String): Future[GameField]
  def save(gameField: GameField, fileName: String): Future[Unit]


