package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import controller.Controller
import controller.impl.DefaultController
import fileIO.fileIOComponents.impl.JsonFileIO
import play.api.libs.json.Json

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

@main def runCoreApi(): Unit =
  implicit val system: ActorSystem = ActorSystem("core-rest-api")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val controller: Controller = DefaultController(using JsonFileIO())

  val helpText =
    """| Willkommen zur Core REST API für "Mensch ärgere dich nicht"!
       |
       |🔧 Verfügbare Endpunkte:
       |  ➤ GET    /api/gamefield        → Aktuelles Spielfeld anzeigen
       |  ➤ POST   /api/dice             → Würfeln
       |  ➤ GET    /api/moves            → Mögliche Züge anzeigen
       |  ➤ POST   /api/move/{index}     → Zug auswählen & ausführen
       |  ➤ POST   /api/undo             → Rückgängig machen
       |  ➤ POST   /api/redo             → Wiederholen
       |
       |💻 Terminal-Testbefehle:
       |  Invoke-WebRequest -Uri http://localhost:8082/api/gamefield
       |  Invoke-WebRequest -Uri http://localhost:8082/api/dice -Method Post
       |  Invoke-WebRequest -Uri http://localhost:8082/api/moves
       |  Invoke-WebRequest -Uri http://localhost:8082/api/move/index -Method Post
       |  Invoke-WebRequest -Uri http://localhost:8082/api/undo -Method Post
       |  Invoke-WebRequest -Uri http://localhost:8082/api/redo -Method Post
       |
       |
       |""".stripMargin

  val route: Route =
    pathPrefix("api") {
      concat(

        // 🏁 Startseite /api oder /api/help
        pathSingleSlash {
          get {
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, helpText))
          }
        },
        path("help") {
          get {
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, helpText))
          }
        },

        path("gamefield") {
          get {
            complete(HttpEntity(ContentTypes.`application/json`, controller.getGameField.toString))
          }
        },

        path("dice") {
          post {
            controller.dice() match
              case Success(_) =>
                complete(HttpResponse(StatusCodes.OK, entity = """{"status": "success", "action": "dice"}"""))
              case Failure(e) =>
                val json = Json.obj("status" -> "error", "message" -> e.getMessage)
                complete(HttpResponse(StatusCodes.Conflict, entity = json.toString()))
          }
        },

        path("undo") {
          post {
            controller.undo() match
              case Success(_) =>
                complete(HttpResponse(StatusCodes.OK, entity = """{"status": "success", "action": "undo"}"""))
              case Failure(e) =>
                val json = Json.obj("status" -> "error", "message" -> e.getMessage)
                complete(HttpResponse(StatusCodes.Conflict, entity = json.toString()))
          }
        },

        path("redo") {
          post {
            controller.redo() match
              case Success(_) =>
                complete(HttpResponse(StatusCodes.OK, entity = """{"status": "success", "action": "redo"}"""))
              case Failure(e) =>
                val json = Json.obj("status" -> "error", "message" -> e.getMessage)
                complete(HttpResponse(StatusCodes.Conflict, entity = json.toString()))
          }
        },

        path("moves") {
          get {
            controller.possibleMoves match
              case Success(moves) =>
                complete(HttpEntity(ContentTypes.`application/json`, moves.mkString("\n")))
              case Failure(e) =>
                val json = Json.obj("status" -> "error", "message" -> e.getMessage)
                complete(HttpResponse(StatusCodes.Conflict, entity = json.toString()))
          }
        },

        path("move" / IntNumber) { index =>
          post {
            controller.possibleMoves match
              case Success(moves) if moves.isDefinedAt(index) =>
                controller.makeMove(moves(index)) match
                  case Success(_) =>
                    complete(HttpResponse(StatusCodes.OK, entity = s"""{"status": "success", "move": "$index"}"""))
                  case Failure(e) =>
                    complete(HttpResponse(StatusCodes.Conflict, entity = s"""{"status": "error", "message": "${e.getMessage}"}"""))
              case Success(_) =>
                complete(HttpResponse(StatusCodes.BadRequest, entity = s"""{"status": "error", "message": "Index out of range"}"""))
              case Failure(e) =>
                complete(HttpResponse(StatusCodes.Conflict, entity = s"""{"status": "error", "message": "${e.getMessage}"}"""))
          }
        }
      )
    }

  val bindingFuture = Http().newServerAt("localhost", 8082).bind(route)
  println("✅ Core REST API läuft auf http://localhost:8082/api/")
  println("➡️ Endpunkte: gamefield, dice, undo, redo, moves, move/{index}")
  println("⏹️ Zum Beenden ENTER drücken...")
  scala.io.StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
