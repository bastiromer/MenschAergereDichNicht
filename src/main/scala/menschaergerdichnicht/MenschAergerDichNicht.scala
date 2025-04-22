package menschaergerdichnicht

import tui.Tui
import controller.Controller
import controller.impl.DefaultController
import fileIO.fileIOComponents.FileIO
import fileIO.fileIOComponents.impl.JsonFileIO


@main def main(): Unit =
  val fileIO: FileIO = JsonFileIO()
  val controller: Controller = DefaultController(using fileIO)
  val tui = Tui(using controller)

  tui.inputLoop()