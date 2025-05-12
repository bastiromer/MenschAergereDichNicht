package fileIO.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import model.modelComponents.GameField
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import model.modelComponents.json.JsonReaders.given
import model.modelComponents.json.JsonWriters.given

class FileIORoutesSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll:
  private val gameField: GameField = GameField.init()
  private val routes: Route = new FileIORoutes().fileIORoutes
  private val fileName: String = "TestField"

  private val gameFieldJson = Json.toJson(gameField)

  override def afterAll(): Unit =
    Await.result(system.terminate(), 10.seconds)

  "FileIORoutes" when {
    "receiving a pre connect request" should {
      "return with StatusCode OK" in {
        Get("/preConnect") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }
    "receiving a save request" should {
      "properly save the field as JSON" in {
        val saveJson = Json.obj(
          "fileName"      -> fileName,
          "gameField" -> gameFieldJson,
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "return an error when the save fails (e.g. due to invalid path)" in {
        val invalidFileName = "/this/path/does/not/exist/field"
        val saveJson = Json.obj(
          "fileName" -> invalidFileName,
          "gameField" -> gameFieldJson,
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("There was an internal server error.")
        }
      }
    }

    "receiving a load request" should {
      "properly load the field as JSON" in {
        val saveJson = Json.obj(
          "fileName" -> fileName,
          "gameField" -> gameFieldJson,
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }

        val loadJson = Json.obj(
          "fileName" -> fileName
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          val gameField: GameField = Json.parse(responseAs[String]).as[GameField]
          gameField shouldBe a[GameField]
        }
      }
      "return InternalServerError on invalid filename" in {
        val loadJson = Json.obj(
          "fileName"   -> "InvalidFilename"
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("There was an internal server error.")
        }
      }
    }

    "receiving a getTarget request" should {
        "return 200 OK and JSON array of targets" in {
          Get("/getTargets") ~> Route.seal(routes) ~> check {
            status shouldBe StatusCodes.OK
          }
        }
    }

    "handle request exception, the exceptionHandler" should {
      "return 404 Not Found for NoSuchElementException on GET request" in {
        Post("/notExistent", "") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.NotFound
          responseAs[String] shouldBe "The requested resource could not be found."
        }
      }
      "return 500 Internal Server Error for unexpected exceptions" in {
        val invalidRequest = "invalidJson"
        Post("/save", invalidRequest) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("Unrecognized token 'invalidJson'")
        }
      }
    }
  }
