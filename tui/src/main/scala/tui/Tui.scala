package tui

import core.controllerComponents.ControllerInterface
import model.modelComponents.Move
import tui.api.service.CoreRequestHttp
import util.Observer

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}


class Tui(using controller: ControllerInterface) extends Observer:
  controller.add(this)
  //println(controller.getGameField.toString)

  def run: Unit =
    update()
    inputLoop()

  override def update(): Unit = println(CoreRequestHttp.getGameField /*controller.getGameField.toString*/)

  def inputLoop(): Unit =
    analyseInput(readLine())
    inputLoop()

  private def analyseInput(input: String): Unit =
    input match
      case "undo" => Try(Await.result(CoreRequestHttp.undo(), 5.seconds)) //controller.undo)
      case "redo" => Try(Await.result(CoreRequestHttp.redo(), 5.seconds)) //controller.redo)
      case "dice" =>
        Await.result(CoreRequestHttp.dice(), 5.seconds)
        update()
      case "move" => findMoves()
      case "load" => load()
      case "save" => save()
      case _ => println(input + " is not a valid command")

  private def findMoves(): Unit = {
    Try(Await.result(CoreRequestHttp.possibleMoves(), 5.seconds)) match
      //controller.possibleMoves match
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
    doAction(() => Try(Await.result(CoreRequestHttp.makeMove(options(input.get)), 5.seconds))) //controller.makeMove(options(input.get)))

  private def load(): Unit =
    doAction(
      action = () => Try(Await.result(CoreRequestHttp.getTargets(), 5.seconds)),
      onSuccess = (list: List[String]) => {
        println("Choose between: " + list.mkString(", "))
        doAction(() => Try(Await.result(CoreRequestHttp.save(readLine()), 5.seconds)))
      }
    )
    /*doAction(
      action = () => {
        print("Choose between: ")
        CoreRequestHttp.getTargets()
        //controller.getTargets
      },
      onSuccess = (list: List[String]) => {
        println(list.mkString(", "))
        doAction(() => controller.load(readLine()))
      }
    )*/

  private def save(): Unit =
    print("target: ")
    doAction(() => Try(Await.result(CoreRequestHttp.save(readLine()), 5.seconds))) //controller.save(readLine()))

  private def doAction[A](action: () => Try[A], onSuccess: A => Unit = (_: A) => ()): Unit = {
    println("doAction")
    action() match {
      case Success(result) => onSuccess(result)
      case Failure(exception) => println(exception.getMessage)
    }
  }