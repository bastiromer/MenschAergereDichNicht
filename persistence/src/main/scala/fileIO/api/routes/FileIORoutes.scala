package fileIO.api.routes

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import model.GameField
import org.slf4j.LoggerFactory
import fileIO.fileIOComponents.impl.JsonFileIO
import play.api.libs.json.{JsValue, Json}
import java.io.File

import util.json.JsonReaders.given
import util.json.JsonWriters.given

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

  private def handleSaveRequest: Route = post {
    path("save") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val filename: String = (jsonValue \ "filename").as[String]
        val fieldValue: GameField = (jsonValue \ "field").as[GameField]
        fileIO.save(fieldValue, filename) match
          case Right(filename) =>
            logger.info(s"Persistence Service [FileIO] -- Field successfully saved to $filename")
            complete(StatusCodes.OK)
          case Left((errMsg, filename)) =>
            logger.error(s"Persistence Service [FileIO] -- Failed to save Field to $filename: $errMsg")
            complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def handleLoadRequest: Route = post {
    path("load") {
      entity(as[String]) { filename =>
        val fullPath = s"$basePath/$filename.json"
        val file = new File(fullPath)

        if file.exists() then {
          val content = Source.fromFile(file).mkString
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))
        } else {
          val json = Json.obj("status" -> "error", "message" -> "File not found")
          complete(HttpResponse(StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`application/json`, json.toString())))
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
