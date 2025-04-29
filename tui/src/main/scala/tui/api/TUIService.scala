package tui.api

object TUIService:
  @main  def startTUIServer(): Unit = tui.api.server.TuiHttpServer.run
