import controller.RestCoreAPI

@main def core(): Unit =
  val restCoreAPI = RestCoreAPI()
  restCoreAPI.start()
