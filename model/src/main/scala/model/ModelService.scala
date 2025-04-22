package model

object ModelService:
  @main def startModelServer(): Unit = model.api.server.ModelHttpServer.run
