import controller.PersistenceControllerInterface
import model.GameField

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

class PersistenceControllerStub extends PersistenceControllerInterface:
  var saveCalls: List[(GameField, String)] = List()
  var loadCalls: List[String] = List()
  var getTargetsResult: List[String] = List()

  override def save(gameField: GameField, target: String): Future[Unit] =
    saveCalls = (gameField, target) :: saveCalls
    Future {}

  override def load(source: String): Future[GameField] =
    loadCalls = source :: loadCalls
    Future { GameField.init() }
  
  override def getTargets: Future[List[String]] =
    Future { getTargetsResult }


