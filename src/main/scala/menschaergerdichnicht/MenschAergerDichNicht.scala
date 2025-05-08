package menschaergerdichnicht

import core.CoreService
import fileIO.PersistenceService
import model.ModelService
import tui.api.TUIService

@main def main(): Unit =
  PersistenceService.startPersistenceServer()
  ModelService.startModelServer()
  CoreService.startCoreServer()
  TUIService.startTUIServer()