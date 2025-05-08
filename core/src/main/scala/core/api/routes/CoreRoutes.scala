package core.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import core.controllerComponents.ControllerInterface
import model.modelComponents.json.JsonWriters.given
import model.modelComponents.json.JsonReaders.given
import model.modelComponents.Move
import org.slf4j.LoggerFactory
import play.api.libs.json.JsPath.\
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success, Try}

class CoreRoutes(val controller: ControllerInterface):
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def coreRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleGetGameField,
      handlePossibleMoves,
      handleMakeMove,
      handleUndo,
      handleRedo,
      handleDice,
      handleSave,
      handleLoad,
      handleGetTargets
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }
  
  private def handleGetGameField: Route = get {
    path("getGameField") {
      complete(Json.toJson(controller.getGameField).toString)
    }
  }
  
  private def handlePossibleMoves: Route = get {
    path("possibleMoves") {
      complete(controller.possibleMoves.toString)
    }
  }
  
  private def handleMakeMove: Route = post {
    path("makeMove") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val move: Move = (jsonValue \ "move").as[Move]
        controller.makeMove(move)
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleUndo: Route = post {
    path("undo") {
      controller.undo()
      complete(StatusCodes.OK)
    }
  }

  private def handleRedo: Route = post {
    path("redo") {
      controller.redo()
      complete(StatusCodes.OK)
    }
  }
  
  private def handleDice: Route = post {
    path("dice") {
      controller.dice()
      complete(StatusCodes.OK)
    }
  }
  
  private def handleSave: Route = post {
    path("save") {
      entity(as[String]) { fileName =>
        controller.save(fileName)
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleLoad: Route = post {
    path("load") {
      entity(as[String]) { fileName =>
        controller.load(fileName)
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleGetTargets: Route = get {
    path("getTargets") {
      complete(controller.getTargets.toString)
    }
  }
  
  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }