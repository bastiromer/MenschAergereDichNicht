package controller

object CoreService:
  @main def startCoreServer(): Unit = controller.api.server.CoreHttpServer.run
