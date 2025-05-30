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
import model.modelComponents.GameField

import scala.io.Source
import scala.util.Using

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

  private def handlePreConnectRequest: Route = get:
    path("preConnect") {
      complete(StatusCodes.OK)
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

  private def handleLoadRequest: Route = post:
    path("load") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val filename: String = (jsonValue \ "fileName").as[String]
        val fullPath = s"$basePath/$filename.json"
        val file = new File(fullPath)
        logger.info(s"Persistence Service [FileIO] -- load call: $filename")

        if file.exists() then {
          val content: String = Using(Source.fromFile(file)) { source =>
            source.mkString
          }.get
          Source.fromFile(file).close()
          logger.info(s"Persistence Service [FileIO] -- Field successfully loaded from $filename")
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))
        } else {
          val json = Json.obj("status" -> "error", "message" -> "File not found")
          logger.error(s"Persistence Service [FileIO] -- Failed to load Field from $filename")
          complete(StatusCodes.InternalServerError)
        }
      }
    }

  private def handleGetTargetsRequest: Route = get:
    path("getTargets") {
      val targets = fileIO.getTargets
      val json = Json.toJson(targets)
      logger.info(s"Persistence Service [FileIO] -- Targets successfully loaded")
      complete(HttpResponse(
        status = StatusCodes.OK,
        entity = HttpEntity(ContentTypes.`application/json`, json.toString())
      ))
    }

  private val exceptionHandler = ExceptionHandler {
    e => complete(InternalServerError -> e.getMessage)
  }
