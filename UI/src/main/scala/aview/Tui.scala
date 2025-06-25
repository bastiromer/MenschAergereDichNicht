package aview

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{Merge, Sink, Source}
import model.{GameField, Move}
import util.{Observable, Observer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}

class Tui(coreController: CoreController):
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "CoreController")

  private val gameFieldSource = coreController.gameFieldStream()
  private val gameFieldSink = Sink.foreach[GameField](println)
  gameFieldSource.to(gameFieldSink).run()

  def inputLoop(): Unit =
    analyseInput(readLine())
    inputLoop()

  private def analyseInput(input: String): Unit =
    input match
      case "undo" => doAction(coreController.undo)
      case "redo" => doAction(coreController.redo)
      case "dice" => doAction(coreController.dice)
      case "move" => findMoves()
      case "load" => load()
      case "save" => save()
      case "newGame" => doAction(coreController.newGame)
      case _ => println(input + " is not a valid command")


  private def findMoves(): Unit =
    try {
      val moves = Await.result(coreController.possibleMoves, 10.seconds)
      doMove(moves)
    } catch {
      case ex: Exception =>
        println(ex.getMessage)
    }

  private def doMove(options: List[Move]): Unit =
    if (options.isEmpty) return
    println("choose between: " + options)
    var input = readLine().toIntOption
    while (options.size <= input.getOrElse(0)) {
      println("choose one move")
      input = readLine().toIntOption
    }
    doAction(() => { coreController.move(options(input.get)) } )

  private def load(): Unit =
    try {
      val targets = Await.result(coreController.getTargets, 10.seconds)
      print("Choose between: ")
      println(targets.mkString(", "))
      doAction(() => coreController.load(readLine()))
    } catch {
      case ex: Exception =>
        println(ex.getMessage)
    }

  private def save(): Unit =
    val target = readLine()
    doAction(() => coreController.save(target))

  private def doAction[A](action: () => Future[A], onSuccess: A => Unit = (_: A) => ()): Unit =
    action().onComplete {
      case Success(result) =>
        onSuccess(result)
      case Failure(exception) => println(exception.getMessage)
    }

