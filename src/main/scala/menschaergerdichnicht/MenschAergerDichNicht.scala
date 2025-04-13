package menschaergerdichnicht

import menschaergerdichnicht.controller.Controller
import menschaergerdichnicht.controller.impl.DefaultController
import menschaergerdichnicht.fileIO.FileIO
import menschaergerdichnicht.fileIO.impl.JsonFileIO
import menschaergerdichnicht.aview.Tui

@main def main(): Unit =
  val fileIO: FileIO = JsonFileIO()
  val controller: Controller = DefaultController(using fileIO)
  val tui = Tui(using controller)

  tui.inputLoop()