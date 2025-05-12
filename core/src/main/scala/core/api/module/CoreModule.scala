package core.api.module

import core.controllerComponents.ControllerInterface
import core.controllerComponents.impl.DefaultController
import fileIO.fileIOComponents.FileIO
import fileIO.fileIOComponents.impl.JsonFileIO

object CoreModule:
  given FileIO = JsonFileIO()
  given ControllerInterface = DefaultController()
