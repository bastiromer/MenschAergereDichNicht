package core

object CoreService:
  @main def startCoreServer(): Unit = core.api.server.CoreHttpServer.run
