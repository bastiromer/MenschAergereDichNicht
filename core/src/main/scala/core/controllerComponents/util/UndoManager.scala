package core.controllerComponents.util

class UndoManager[T]:
  private var undoStack: List[Command[T]] = Nil
  private var redoStack: List[Command[T]] = Nil
  def doStep(t: T, command: Command[T]): T =
    undoStack = command :: undoStack
    command.doStep(t)

  def undoStep(t: T): T =
    undoStack match {
      case Nil => t
      case head :: stack =>
        val result = head.undoStep(t) 
        undoStack = stack
        redoStack = head :: redoStack
        result
    }

  def redoStep(t: T): T =
    redoStack match {
      case Nil => t
      case head :: stack =>
        val result = head.redoStep(t)
        redoStack = stack
        undoStack = head :: undoStack
        result
    }

  def clear(): Unit =
    undoStack = List()
    redoStack = List()
