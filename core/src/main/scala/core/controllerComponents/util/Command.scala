package core.controllerComponents.util

trait Command[T]:
  def doStep(t: T): T
  def undoStep(t: T): T
  def redoStep(t: T): T
