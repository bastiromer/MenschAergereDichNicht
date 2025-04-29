package tui.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import tui.Tui

class TUIRoutes(val tui: Tui):
  def tuiRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleEventRequests
    )
  }

  private def handleEventRequests: Route = get {
    pathPrefix("update") {
      parameter("event") {
        case "undo" =>
          
          complete(StatusCodes.OK)
        case "redo" =>
          
          complete(StatusCodes.OK)
        case "dice" =>
          
          complete(StatusCodes.OK)
        case "move" =>

          complete(StatusCodes.OK)
        case "load" =>

          complete(StatusCodes.OK)
        case "save" =>

          complete(StatusCodes.OK)
        case _ =>
          complete(BadRequest, "Invalid event")
      }
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(NotFound -> e.getMessage)
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
