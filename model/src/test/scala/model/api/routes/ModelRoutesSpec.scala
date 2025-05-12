package model.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import model.modelComponents.{Cell, GameField, Move}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

import model.modelComponents.json.JsonReaders.given
import model.modelComponents.json.JsonWriters.given

class ModelRoutesSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private val routes: Route = new ModelRoutes().modelRoutes
  private val gameField: GameField = GameField.init()
  private val gameFieldJson = Json.toJson(gameField)
  private val move = Move(0, 1)
  private val moveJson = Json.toJson(move)

  override def afterAll(): Unit =
    system.terminate()

  "ModelRoutes" should {

    "respond to preConnect with OK" in {
      Get("/preConnect") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "handle /possibleMoves" in {
      val payload = Json.obj("field" -> gameFieldJson).toString
      Get("/possibleMoves", payload) ~> routes ~> check {
        val possibleMoves: List[Move] = Json.parse(responseAs[String]).as[List[Move]]
        status shouldBe StatusCodes.OK
        possibleMoves shouldBe a[List[Move]]
      }
    }

    "handle /fromString" in {
      val input = "r"
      Post("/fromString", input) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should be("Red")
      }
    }

    "handle /toCell" in {
      val json = Json.obj(
        "field" -> gameFieldJson,
        "move"  -> moveJson
      ).toString
      Post("/toCell", json) ~> routes ~> check {
        val cell: Cell = Json.parse(responseAs[String]).as[Cell]
        status shouldBe StatusCodes.OK
        cell shouldBe a[Cell]
      }
    }

    "handle /gameFieldinit" in {
      Post("/gameFieldinit") ~> routes ~> check {
        val field = Json.parse(responseAs[String]).as[GameField]
        status shouldBe StatusCodes.OK
        field shouldBe a[GameField]
      }
    }

    "handle /rollDice" in {
      val json = Json.obj("field" -> gameFieldJson).toString
      Post("/rollDice", json) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val rolled = Json.parse(responseAs[String]).as[GameField]
        rolled shouldBe a[GameField]
      }
    }

    "handle /move" in {
      val json = Json.obj(
        "move" -> moveJson,
        "field" -> gameFieldJson
      ).toString
      Post("/move", json) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val movedField = Json.parse(responseAs[String]).as[GameField]
        movedField shouldBe a[GameField]
      }
    }

    "return 409 Conflict for IllegalArgumentException in /fromString" in {
      val invalidInput = "x"
      Post("/fromString", invalidInput) ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.Conflict
        responseAs[String] should include("Invalid player initial")
      }
    }

    "return 500 for invalid JSON" in {
      val invalidJson = "invalid"
      Post("/move", invalidJson) ~> Route.seal(routes) ~> check {
        status shouldBe StatusCodes.InternalServerError
        responseAs[String] should include("Unrecognized token")
      }
    }
  }
}
