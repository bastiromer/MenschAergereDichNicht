package core.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import core.controllerComponents.ControllerInterface
import core.controllerComponents.util.ObserverHttp
import model.modelComponents.json.JsonWriters.given
import model.modelComponents.json.JsonReaders.given
import model.modelComponents.Move
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success}

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
      handleGetTargets,
      handleRegisterObserver,
      handleDeregisterObserver
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
      controller.possibleMoves match
      case Success(targets) =>
        val json = Json.toJson(targets).toString()
        complete(json)
      case Failure(exception) =>
        val json = Json.obj("status" -> "error", "message" -> "No targets found")
        complete(StatusCodes.InternalServerError)
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
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fileName: String = (jsonValue \ "target").as[String]
        controller.save(fileName)
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleLoad: Route = post {
    path("load") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fileName: String = (jsonValue \ "source").as[String]
        controller.load(fileName)
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleGetTargets: Route = get {
    path("getTargets") {
      controller.getTargets match
        case Success(targets) =>
          val json = Json.toJson(targets).toString()
          complete(json)
        case Failure(exception) =>
          val json = Json.obj("status" -> "error", "message" -> "No targets found")
          complete(StatusCodes.InternalServerError)
    }
  }
  
  private def handleRegisterObserver: Route = post {
    path("registerObserver") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val observerUrl: String = (jsonValue \ "url").as[String]
        controller.add(new ObserverHttp(observerUrl))
        logger.info(s"Observer registered at: $observerUrl")
        complete(StatusCodes.OK)
      }
    }
  }
  
  private def handleDeregisterObserver: Route = post {
    path("deregisterObserver") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val observerUrl: String = (jsonValue \ "url").as[String]
        controller.remove(observerUrl)
        logger.info(s"Observer deregistered from: $observerUrl")
        complete(StatusCodes.OK)
      }
    }
  }
  
  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }