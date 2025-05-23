package fileIO.fileIOComponents

import fileIO.fileIOComponents.FileIO
import model.modelComponents.GameField

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileIOStub extends FileIO:
  var saveCalls: List[(GameField, String)] = List()
  var loadCalls: List[String] = List()
  var getTargetsResult: List[String] = List()

  def save(gameField: GameField, target: String): Either[(String, String), String] = {
    saveCalls = (gameField, target) :: saveCalls
    Right(target + ".json")
  }

  def load(source: String): Future[GameField]=
    loadCalls = source :: loadCalls
    Future(GameField.init())


  def getTargets: List[String] =
    getTargetsResult

