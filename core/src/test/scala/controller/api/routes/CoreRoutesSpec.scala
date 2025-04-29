package controller.api.routes

import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import controller.controllerComponents.ControllerInterface
import model.modelComponents.{GameField, Move}
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import model.modelComponents.json.JsonReaders.given
import model.modelComponents.json.JsonWriters.given

import scala.util.Success

class CoreRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach:

  val controller: ControllerInterface = mock(classOf[ControllerInterface])
  val routes: Route = new CoreRoutes(controller).coreRoutes

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(controller)
  }

  "CoreRoutes" should {

    "return OK on /preConnect" in {
      Get("/preConnect") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "return game field as JSON on /getGameField" in {
      when(controller.getGameField).thenReturn(GameField.init())
      Get("/getGameField") ~> routes ~> check {
        val field = Json.parse(responseAs[String]).as[GameField]
        status shouldBe StatusCodes.OK
        field shouldBe a[GameField]
      }
    }

    "return possible moves on /possibleMoves" in {
      when(controller.possibleMoves).thenReturn(Success(List(Move(1, 2))))
      Get("/possibleMoves") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    /*
    "call makeMove on /makeMove" in {
      val moveJson = Json.obj("move" -> Json.obj("x" -> 1, "y" -> 2)).toString()
      Post("/makeMove", HttpEntity(ContentTypes.`application/json`, moveJson)) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).makeMove(Move(1, 2)) // Passen je nach Move-Konstruktor
      }
    }
     */

    "call undo on /undo" in {
      Post("/undo") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).undo()
      }
    }

    "call redo on /redo" in {
      Post("/redo") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).redo()
      }
    }

    "call dice on /dice" in {
      Post("/dice") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).dice()
      }
    }

    "call save on /save" in {
      Post("/save", HttpEntity(ContentTypes.`text/plain(UTF-8)`, "testFile")) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).save("testFile")
      }
    }

    "call load on /load" in {
      Post("/load", HttpEntity(ContentTypes.`text/plain(UTF-8)`, "testFile")) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        verify(controller).load("testFile")
      }
    }

    /*
    "return targets on /getTargets" in {
      when(controller.getTargets).thenReturn("target1,target2")
      Get("/getTargets") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include("target1")
      }
    }
     */

    "handle IllegalArgumentException with 409" in {
      when(controller.getGameField).thenThrow(new IllegalArgumentException("Invalid"))
      Get("/getGameField") ~> routes ~> check {
        status shouldBe StatusCodes.Conflict
        responseAs[String] should include("Invalid")
      }
    }

    "handle unknown exception with 500" in {
      when(controller.getTargets).thenThrow(new RuntimeException("Boom"))
      Get("/getTargets") ~> routes ~> check {
        status shouldBe StatusCodes.InternalServerError
        responseAs[String] should include("Boom")
      }
    }
  }
