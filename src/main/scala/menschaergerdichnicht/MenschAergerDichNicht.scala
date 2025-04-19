package menschaergerdichnicht

import fileIO.FileIO
import fileIO.impl.JsonFileIO
import tui.Tui
import controller.Controller
import controller.impl.DefaultController


@main def main(): Unit =
  val fileIO: FileIO = JsonFileIO()
  val controller: Controller = DefaultController(using fileIO)
  val tui = Tui(using controller)

  tui.inputLoop()