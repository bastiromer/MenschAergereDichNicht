package util

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{ExecutionContextExecutor, Future}

def handleResponse[T](response: HttpResponse)(block: String => T): Future[T] = {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  
  response.status match {
    case StatusCodes.OK =>
      Unmarshal(response.entity).to[String].map { jsonStr =>
        block(jsonStr)
      }
    case _ =>
      val errorMessage = response.entity match {
        case HttpEntity.Strict(_, data) => data.utf8String
        case _ => "Unknown error occurred"
      }
      Future.failed(new RuntimeException(s"${response.status}: $errorMessage"))
  }
}
