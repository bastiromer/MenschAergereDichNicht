import FileIO.RestPersistenceAPI

@main def persistence(): Unit = {
  val restPersistenceAPI = RestPersistenceAPI()
  restPersistenceAPI.start()
}


