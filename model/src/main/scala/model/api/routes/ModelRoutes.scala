package model.api.routes

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.StatusCodes.*
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import model.modelComponents.*
import model.modelComponents.json.JsonReaders.given
import model.modelComponents.json.JsonWriters.given
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}


class ModelRoutes:
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def modelRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handlePossibleMoves,
      handleFromString,
      handleToCell,
      handleGameFieldinit,
      handleRollDice,
      handleMove
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handlePossibleMoves: Route = post {
    path("possibleMoves") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val gameField: GameField = (jsonValue \ "field").as[GameField]
        val content = GameField(gameField.map, gameField.gameState).possibleMoves()
        logger.info(s"Model Service [Model] -- Possible moves returned")
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, Json.toJson(content).toString)))
      }
    }
  }

  private def handleFromString: Route = post {
    path("fromString") {
      entity(as[String]) { str =>
        val player = Player
        val target = player.fromString(str)
        logger.info(s"Model Service [Model] -- fromString")
        complete(HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`, target.toString)
        ))
      }
    }
  }

  private def handleToCell: Route = post {
    path("toCell") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val gameField: GameField = (jsonValue \ "field").as[GameField]
        val move: Move = (jsonValue \ "move").as[Move]
        val cell: Cell = move.toCell(gameField.map)
        val content = Json.toJson(cell)
        logger.info(s"Model Service [Model] -- toCell")
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content.toString)))
      }
    }
  }

  private def handleGameFieldinit: Route = post {
    path("gameFieldinit") {
      val gameField: GameField = GameField.init()
      val content = Json.toJson(gameField)
      logger.info(s"Model Service [Model] -- init gameField")
      complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content.toString)))
    }
  }

  private def handleRollDice: Route = post {
    path("rollDice") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val gameField: GameField = (jsonValue \ "field").as[GameField].rollDice
        val content = Json.toJson(gameField).toString()
        logger.info(s"Model Service [Model] -- roll dice")
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))
      }
    }
  }

  private def handleMove: Route = post {
    path("move") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val move: Move = (jsonValue \ "move").as[Move]
        val gameField: GameField = (jsonValue \ "field").as[GameField]
        val content: GameField = gameField.move(move)
        logger.info(s"Model Service [Model] -- handle move")
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, Json.toJson(content).toString)))
      }
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
