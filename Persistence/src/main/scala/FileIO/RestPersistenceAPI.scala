package FileIO


import FileIO.JsonFileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import model.GameField
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class RestPersistenceAPI:
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "RestPersistenceAPI")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  var fileIO = new JsonFileIO
  private val RestUIPort = 8081
  private val routes: String =
    """
      <h1>Welcome to the REST Persistence API service!</h1>
      <h2>Available routes:</h2>

      <p><a href="persistence/save">POST      ->     persistence/save</a></p>
      <p><a href="persistence/load">GET       ->     persistence/load</a></p>
      <p><a href="persistence/getTargets">GET ->     persistence/getTargets</a></p>

      <br>
    """.stripMargin

  private val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      path("persistence" / "save") {
        post {
          entity(as[String]) { saveRequest =>
            parameter("file".as[String]) { fileName =>
              val gameField: GameField = Json.fromJson(Json.parse(saveRequest)).get
              fileIO.save(gameField, fileName)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          }
        }
      },
      get {
        path("persistence" / "load") {
          parameter("file".as[String]) { fileName =>
            try {
              val game = fileIO.load(fileName)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Json.toJson(game).toString()))
            } catch
              case ex: Exception =>
                complete(HttpResponse(StatusCodes.Conflict, entity = ex.getMessage))
          }
        }
      },
      get {
        path("persistence" / "getTargets") {
          val targets = fileIO.getTargets
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Json.toJson(targets).toString()))
        }
      },
    )

  def start(): Unit =
    val binding = Http().newServerAt("0.0.0.0", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) =>
        println(s"PersistenceAPI service online at http://localhost:$RestUIPort/")
      case Failure(exception) =>
        println(s"PersistenceAPI service failed to start: ${exception.getMessage}")
    }
