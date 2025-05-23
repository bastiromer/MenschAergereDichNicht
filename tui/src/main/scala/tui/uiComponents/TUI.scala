package tui.uiComponents

import model.modelComponents.Move
import tui.api.service.CoreRequestHttp

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI:

  def run(): Unit =
    update()
    inputLoop()

  def update(): Unit = println(CoreRequestHttp.getGameField)

  private def inputLoop(): Unit =
    analyseInput(readLine())
    inputLoop()

  private def analyseInput(input: String): Unit =
    input match
      case "undo" => Try(Await.result(CoreRequestHttp.undo(), 5.seconds))
      case "redo" => Try(Await.result(CoreRequestHttp.redo(), 5.seconds))
      case "dice" => Await.result(CoreRequestHttp.dice(), 5.seconds)
      case "move" => findMoves()
      case "load" => load()
      case "save" => save()
      case _ => println(input + " is not a valid command")

  private def findMoves(): Unit = {
    Try(Await.result(CoreRequestHttp.possibleMoves(), 5.seconds)) match
      case Failure(exception) => println(exception.getMessage)
      case Success(moves) => doMove(moves)
  }

  private def doMove(options: List[Move]): Unit =
    if (options.isEmpty) return
    println("choose between: " + options)
    var input = readLine().toIntOption
    while (options.size <= input.getOrElse(0)) {
      println("choose one move")
      input = readLine().toIntOption
    }
    doAction(() => Try(Await.result(CoreRequestHttp.makeMove(options(input.get)), 5.seconds)))

  private def load(): Unit =
    doAction(
      action = () => Try(Await.result(CoreRequestHttp.getTargets, 5.seconds)),
      onSuccess = (list: List[String]) => {
        println("Choose between: " + list.mkString(", "))
        doAction(() => Try(Await.result(CoreRequestHttp.load(readLine()), 5.seconds)))
      }
    )
    
  private def save(): Unit =
    print("target: ")
    doAction(() => Try(Await.result(CoreRequestHttp.save(readLine()), 5.seconds)))

  private def doAction[A](action: () => Try[A], onSuccess: A => Unit = (_: A) => ()): Unit = {
    println("doAction")
    action() match {
      case Success(result) => onSuccess(result)
      case Failure(exception) => println(exception.getMessage)
    }
  }