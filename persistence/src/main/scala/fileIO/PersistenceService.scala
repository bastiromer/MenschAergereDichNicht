package fileIO

object PersistenceService:
  @main def startPersistenceServer(): Unit = fileIO.api.server.PersistenceHttpServer.run