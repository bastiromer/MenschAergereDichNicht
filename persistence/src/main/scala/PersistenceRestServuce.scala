package persistence

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import fileIO.impl.JsonFileIO
import model.GameField
import play.api.libs.json.Json

import scala.io.Source
import scala.io.StdIn
import java.io.File
import scala.util.{Success, Failure, Try}


import util.json.JsonReaders.given
import util.json.JsonWriters.given

@main def runPersistenceApi(): Unit =
  implicit val system: ActorSystem = ActorSystem("persistence-rest-api")
  import system.dispatcher

  val fileIO = JsonFileIO()
  val basePath = "saveGameJson"

  val route =
    pathPrefix("api") {
      concat(


        path("persistence" / "save") {
          post {
            entity(as[String]) { saveRequest =>
              parameter("file".as[String]) { fileName =>
                Try(Json.parse(saveRequest).as[GameField]) match {
                  case Success(gameField) =>
                    Try(fileIO.save(gameField, fileName)) match {
                      case Success(_) =>
                        val json = Json.obj("status" -> "ok", "message" -> s"Saved '$fileName'")
                        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, json.toString())))
                      case Failure(e) =>
                        val json = Json.obj("status" -> "error", "message" -> e.getMessage)
                        complete(HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(ContentTypes.`application/json`, json.toString())))
                    }
                  case Failure(_) =>
                    val json = Json.obj("status" -> "error", "message" -> "Invalid JSON")
                    complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`application/json`, json.toString())))
                }
              }
            }
          }
        },

        path("persistence" / "getTargets") {
          get {
            val targets = fileIO.getTargets
            val json = Json.toJson(targets)
            complete(HttpResponse(
              status = StatusCodes.OK,
              entity = HttpEntity(ContentTypes.`application/json`, json.toString())
            ))
          }
        },


        path("load" / Segment) { filename =>
          get {
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
      )
    }

  val binding = Http().newServerAt("localhost", 8083).bind(route)

  println("💾 Persistence REST API läuft unter http://localhost:8083/api/")
  println("Zum Beenden ENTER drücken...")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
