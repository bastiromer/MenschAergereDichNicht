package aview

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import model.kafka.Topics
import model.{GameField, Move}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*
import util.{Observable, establishWebSocketConnection, handleResponse, sendHttpRequest}

import java.util.Properties
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class CoreController:
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "CoreController")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  private val topic = Topics.GameTopic

  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("kafka1:9092")
    .withGroupId("KafkaCoreController")
    .withProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "KafkaCoreController-id")


  private val source = Consumer.plainSource(
    consumerSettings,
    Subscriptions.topics(topic.toString)
  )

  private val gameFieldSink = Sink.foreach[ConsumerRecord[String, String]] { record =>
    gameFieldQueue.offer(Json.parse(record.value()).as[GameField])
  }

  source.to(gameFieldSink).run()

  private val (gameFieldQueue, gameFieldSource) =
    Source.queue[GameField](
      bufferSize = 100,
      overflowStrategy = OverflowStrategy.dropHead
    ).preMaterialize()

  def gameFieldStream(): Source[GameField, NotUsed] = gameFieldSource

  def possibleMoves: Future[List[Move]] =
    val request = HttpRequest(uri = "http://core-service:8082/core/possibleMoves")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[List[Move]])
    }

  def move(move: Move): Future[Unit] =
    val jsonBody = Json.toJson(move).toString()
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/move",
      entity = HttpEntity(ContentTypes.`application/json`, jsonBody)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def dice(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/dice"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def undo(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/undo"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def redo(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/redo"
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def save(fileName: String): Future[Unit] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://core-service:8082/core/save",
      entity = HttpEntity(ContentTypes.`application/json`, fileName)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }
  }

  def load(fileName: String): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/load",
      entity = HttpEntity(ContentTypes.`application/json`, fileName)
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def newGame(): Future[Unit] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://core-service:8082/core/newGame",
      entity = HttpEntity(ContentTypes.`application/json`, "")
    )
    sendHttpRequest(request).map { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr))
    }

  def getTargets: Future[List[String]] =
    val request = HttpRequest(uri = "http://core-service:8082/core/getTargets")
    sendHttpRequest(request).flatMap { response =>
      handleResponse(response)(jsonStr => Json.parse(jsonStr).as[List[String]])
    }
