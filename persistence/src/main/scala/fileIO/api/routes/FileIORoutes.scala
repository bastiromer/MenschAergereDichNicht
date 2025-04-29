package fileIO.api.routes

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import org.slf4j.LoggerFactory
import fileIO.fileIOComponents.impl.JsonFileIO
import play.api.libs.json.{JsValue, Json}
import java.io.File

import model.modelComponents.json.JsonReaders.given
import model.modelComponents.json.JsonWriters.given
import model.modelComponents.GameField

import scala.io.Source

class FileIORoutes:
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  private val fileIO = JsonFileIO()
  private val basePath = "saveGameJson"

  def fileIORoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleSaveRequest,
      handleLoadRequest,
      handleGetTargetsRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handleSaveRequest: Route = post:
    path("save") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val filename: String = (jsonValue \ "fileName").as[String]
        val gameField: GameField = (jsonValue \ "gameField").as[GameField]
        fileIO.save(gameField, filename) match
          case Right(filename) =>
            logger.info(s"Persistence Service [FileIO] -- Field successfully saved to $filename")
            complete(StatusCodes.OK)
          case Left((errMsg, filename)) =>
            logger.error(s"Persistence Service [FileIO] -- Failed to save Field to $filename: $errMsg")
            complete(StatusCodes.InternalServerError)
      }
    }

  private def handleLoadRequest: Route = post {
    path("load") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val filename: String = (jsonValue \ "fileName").as[String]
        val fullPath = s"$basePath/$filename.json"
        val file = new File(fullPath)

        if file.exists() then {
          val content = Source.fromFile(file).mkString
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))
        } else {
          val json = Json.obj("status" -> "error", "message" -> "File not found")
          complete(StatusCodes.InternalServerError)
          //complete(HttpResponse(StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`application/json`, json.toString())))
        }
      }
    }
  }

  private def handleGetTargetsRequest: Route = get {
    path("getTargets") {
      val targets = fileIO.getTargets
      val json = Json.toJson(targets)
      complete(HttpResponse(
        status = StatusCodes.OK,
        entity = HttpEntity(ContentTypes.`application/json`, json.toString())
      ))
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
