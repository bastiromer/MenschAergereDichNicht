package util

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

def establishWebSocketConnection(wsUrl: String, messageHandler: Message => Unit): Future[Unit] = {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "establishWebSocketConnection")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  
  val (upgradeResponse, _) = Http().singleWebSocketRequest(
    WebSocketRequest(wsUrl),
    Flow.fromSinkAndSourceMat(
      Sink.foreach[Message](messageHandler),
      Source.queue[TextMessage](10, OverflowStrategy.fail)
    )(Keep.both)
  )

  upgradeResponse.flatMap {
    case upgrade if upgrade.response.status == StatusCodes.SwitchingProtocols =>
      println("WebSocket connection established")
      Future.successful(())
    case upgrade =>
      println(s"WebSocket connection failed: ${upgrade.response.status}")
      Future.failed(new RuntimeException(s"WebSocket connection failed: ${upgrade.response.status}"))
  }
}

 
