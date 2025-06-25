import controller.PersistenceControllerInterface
import model.GameField

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

class PersistenceControllerStub extends PersistenceControllerInterface:
  var saveCalls: List[(GameField, String)] = List()
  var loadCalls: List[String] = List()
  var getTargetsResult: List[String] = List()
  var saveDatabaseCalls: List[GameField] = List()
  var updateDatabaseCalls: List[GameField] = List()
  var loadDatabaseCalls = 0
  var deleteDataBaseCalls = 0
  

  override def save(gameField: GameField, target: String): Future[Unit] =
    saveCalls = (gameField, target) :: saveCalls
    Future {}

  override def load(source: String): Future[GameField] =
    loadCalls = source :: loadCalls
    Future { GameField.init() }
  
  override def getTargets: Future[List[String]] =
    Future { getTargetsResult }

  override def databaseLoad: Future[GameField]  =
    loadDatabaseCalls += 1
    Future { GameField.init() }

  override def databaseSave(gameField: GameField): Future[Unit] =
    saveDatabaseCalls = gameField :: saveDatabaseCalls
    Future {}

  override def databaseUpdate(gameField: GameField): Future[Unit] =
    updateDatabaseCalls = gameField :: updateDatabaseCalls
    Future {}

  override def databaseDelete(): Future[Unit] =
    deleteDataBaseCalls += 1
    Future {}
    
