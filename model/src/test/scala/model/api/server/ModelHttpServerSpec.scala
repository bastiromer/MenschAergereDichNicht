package model.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCode, StatusCodes}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class ModelHttpServerSpec extends AnyWordSpec with BeforeAndAfterAll:
  private implicit val system: ActorSystem = ActorSystem("ModelHttpServerTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val MODEL_BASE_URL = "http://localhost:8081/"

  private var testModelServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(ModelHttpServer.run, 10.seconds))

  override def afterAll(): Unit =
    testModelServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "ModelHttpServer" should {
    "return OK on model pre connect request when started" in {
      val futureResponse: Future[StatusCode] = Http()
        .singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = MODEL_BASE_URL.concat("api/model/field/preConnect")
          )
        ).map { response => response.status }
      val response: StatusCode = Await.result(futureResponse, 5.seconds)
      response shouldBe StatusCodes.OK
    }
    "handle double binding failure during server startup" in {
      val exception = intercept[Exception] {
        Await.result(ModelHttpServer.run, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(ModelHttpServer.system).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }

