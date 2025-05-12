package tui.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import tui.uiComponents.TUI

class TUIRoutes(val tui: TUI):
  def tuiRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleUpdate
    )
  }

  private def handleUpdate: Route = get {
    pathPrefix("update") {
      tui.update()
      complete(StatusCodes.OK)
    }
  }

  private val exceptionHandler = ExceptionHandler {
    e => complete(InternalServerError -> e.getMessage)
  }
