package menschaergerdichnicht

import menschaergerdichnicht.model.GameField
import menschaergerdichnicht.fileIO.FileIO
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FileIOStub extends FileIO:
  var saveCalls: List[(GameField, String)] = List()
  var loadCalls: List[String] = List()
  var getTargetsResult: List[String] = List()

  def save(gameField: GameField, target: String): Unit =
    saveCalls = (gameField, target) :: saveCalls

  def load(source: String): Future[GameField]=
    loadCalls = source :: loadCalls
    Future(GameField.init())


  def getTargets: List[String] =
    getTargetsResult

