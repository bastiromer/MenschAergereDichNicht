package controller.api.module

import controller.controllerComponents.ControllerInterface
import controller.controllerComponents.impl.DefaultController
import fileIO.fileIOComponents.FileIO
import fileIO.fileIOComponents.impl.JsonFileIO

object CoreModule:
  given FileIO = JsonFileIO()
  given ControllerInterface = DefaultController()
