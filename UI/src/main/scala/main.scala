import aview.{CoreController, Tui}

@main def ui(): Unit =
  val coreController = CoreController()
  val tui = Tui(coreController)
  tui.inputLoop()
