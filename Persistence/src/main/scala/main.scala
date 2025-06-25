import Persistence.DB.DAOInterface
import Persistence.DB.mongo.Mongo
import Persistence.DB.slick.Slick
import Persistence.FileIO.{FileIO, JsonFileIO}
import Persistence.RestPersistenceAPI
import model.GameField

@main def persistence(): Unit = {
  val fileIO: FileIO = JsonFileIO()
  val database: DAOInterface = Mongo()
  
  val restPersistenceAPI = RestPersistenceAPI(using fileIO)(using database)
  restPersistenceAPI.start()
}


