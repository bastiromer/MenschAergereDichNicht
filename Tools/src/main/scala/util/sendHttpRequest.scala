package util

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, StatusCodes}

import scala.concurrent.{ExecutionContextExecutor, Future}

def sendHttpRequest(request: HttpRequest, retries: Int = 3): Future[HttpResponse] = {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  
  val responseFuture = Http().singleRequest(request)
  responseFuture.flatMap { response =>
    response.status match {
      case StatusCodes.OK => Future.successful(response)
      case _ if retries > 0 =>
        response.discardEntityBytes()
        sendHttpRequest(request, retries - 1)
      case _ =>
        val errorMessage = response.entity match {
          case HttpEntity.Strict(_, data) => data.utf8String
          case _ => "Unknown error occurred"
        }
        Future.failed(new RuntimeException(s"${response.status}: $errorMessage"))
    }
  }
}

 
