import Persistence.DB.Slick
import Persistence.RestPersistenceAPI
import model.GameField

@main def persistence(): Unit = {
  val restPersistenceAPI = RestPersistenceAPI()
  restPersistenceAPI.start()
}


