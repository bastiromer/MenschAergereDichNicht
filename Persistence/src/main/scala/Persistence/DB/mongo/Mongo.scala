package Persistence.DB.mongo

import Persistence.DB.DAOInterface
import com.mongodb.client.model.Updates.combine
import model.{Cell, GameField, GameState, Player}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.gridfs.{ObservableFuture, SingleObservableFuture}
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, ObservableFuture, Observer, SingleObservable, SingleObservableFuture, result}
import play.api.libs.json.Json
import util.json.JsonReaders.*
import util.json.JsonWriters.*

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class Mongo extends DAOInterface:
  private val database_pw = sys.env.getOrElse("MONGO_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "mongoDB")
  //private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  private val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  private val db: MongoDatabase = client.getDatabase("tbl")
  private val gameCollection: MongoCollection[Document] = db.getCollection("game")

  def save(gameField: GameField): Unit =
    try {
      val document = Document(
        "_id" -> (getHighestId(gameCollection) + 1),
        "state" -> Document(
          "shouldDice" -> gameField.gameState.shouldDice,
          "diceNumber" -> gameField.gameState.diceNumber,
          "currentPlayer" -> gameField.gameState.currentPlayer.toString
        ),
        "map" -> Json.toJson(gameField).toString()
      )
      handleResult(gameCollection.insertOne(document))
    } catch
      case ex: Exception => println(ex)


  def load(): GameField =
    try {
      val filter = equal("_id", getHighestId(gameCollection))
      val future = gameCollection.find(filter).first().toFutureOption()
      val gameDocument = Await.result(future, 10.seconds)
      val map = Json.parse(gameDocument.get("map").asString().getValue).as[GameField].map
      val state = queryState(gameDocument.get("state"))
      GameField.init().copy(map = map, gameState = state)
    } catch
      case ex: Exception =>
        GameField.init()

  def update(gameField: GameField): Unit = {
    val filter = equal("_id", getHighestId(gameCollection))
    val update = Updates.combine(
      Updates.set("state.shouldDice", gameField.gameState.shouldDice),
      Updates.set("state.diceNumber", gameField.gameState.diceNumber),
      Updates.set("state.currentPlayer", gameField.gameState.currentPlayer.toString),
      Updates.set("map", Json.toJson(gameField).toString())
    )
    handleUpdateResult(gameCollection.updateOne(filter, update))
  }

  def delete(): Unit =
    val filter = Document()
    handleDeleteResult(gameCollection.deleteMany(filter))

  private def queryState(stateDoc: BsonDocument): GameState =
    val shouldDice = stateDoc.get("shouldDice").asBoolean().getValue
    val diceNumber = stateDoc.get("diceNumber").asInt32().getValue
    val currentPlayer = stateDoc.get("currentPlayer").asString().getValue
    GameState(shouldDice, diceNumber, Player.fromString(currentPlayer))

  private def getHighestId(coll: MongoCollection[Document]): Int =
    val result = Await.result(coll.aggregate(Seq(
      Aggregates.sort(Sorts.descending("_id")),
      Aggregates.limit(1),
      Aggregates.project(Document("_id" -> 1))
    )).headOption(), Inf)
    result.flatMap(_.get("_id").map(_.asInt32().getValue.toString)).getOrElse("0").toInt

  private def handleResult[T](obs: SingleObservable[T]): Unit =
    Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), 10.seconds)
    println("db operation successful")

  private def handleUpdateResult(obs: SingleObservable[UpdateResult]): Unit =
    Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), 10.seconds)
    println("Update successful")

  private def handleDeleteResult(obs: SingleObservable[DeleteResult]): Unit =
    Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), 10.seconds)
    println("Delete successful")

