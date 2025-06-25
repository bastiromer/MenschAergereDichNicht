package Persistence.DB

import akka.japi.JAPI
import model.{Cell, GameField, GameState, Player}
import play.api.libs.json.Json
import slick.dbio.Effect.Read
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.TableQuery
import util.json.JsonReaders.*
import util.json.JsonWriters.*

import java.sql.SQLNonTransientException
import scala.concurrent.Await
import scala.util.{Failure, Success}
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class Slick extends DAOInterface:
  private val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "tbl")
  private val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "postgres")
  private val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "5432")
  //private val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "localhost")
  private val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "database")
  private val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

  private val WAIT_TIME = 5.seconds


  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  private val gameFieldTable = TableQuery(GameFieldTable(_))
  private val gameStateTable = TableQuery(GameStateTable(_))
  private val mapTable = TableQuery(MapTable(_))

  private val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(
    mapTable.schema.createIfNotExists,
    gameStateTable.schema.createIfNotExists,
    gameFieldTable.schema.createIfNotExists
  )
  println("create tables")

  database.run(setup).onComplete {
    case Success(value) => println("tables created")
    case Failure(exception) => println(exception.getMessage)
  }

  def save(gameField: GameField): Unit =
    val gameStateInsert = (gameStateTable returning gameStateTable.map(_.id)) += (
      None,
      gameField.gameState.shouldDice,
      gameField.gameState.diceNumber,
      gameField.gameState.currentPlayer.toString
    )
    val gameStateId = Await.result(database.run(gameStateInsert), WAIT_TIME)
    val mapInsert = (mapTable returning mapTable.map(_.id)) += (None, Json.toJson(gameField).toString())
    val mapId = Await.result(database.run(mapInsert), WAIT_TIME)
    val gameFieldInsert = (gameFieldTable returning gameFieldTable.map(_.id)) into ((_, id) => id) += (
      None,
      gameStateId.get,
      mapId.get
    )
    Await.result(database.run(gameFieldInsert), WAIT_TIME)
    println("Database save")

  def update(gameField: GameField): Unit =
    val gameFieldQuery = gameFieldTable.result
    val gameFieldResult = Await.result(database.run(gameFieldQuery), WAIT_TIME).head
    val gameStateUpdate = gameStateTable
      .filter(_.id === gameFieldResult._2)
      .update(
        None,
        gameField.gameState.shouldDice,
        gameField.gameState.diceNumber,
        gameField.gameState.currentPlayer.toString
      )
    val mapUpdate = mapTable
      .filter(_.id === gameFieldResult._3)
      .update(None, Json.toJson(gameField).toString())
    val updateActions = for {
      _ <- gameStateUpdate
      _ <- mapUpdate
    } yield ()
    Await.result(database.run(updateActions.transactionally), WAIT_TIME)
    println("Database updated")

  def load(): GameField =
    val gameFieldQuery = gameFieldTable
      .join(gameStateTable).on(_.stateId === _.id)
      .join(mapTable).on(_._1.mapId === _.id)
      .sortBy(_._1._1.id.desc)
      .take(1)
    val action = gameFieldQuery.result
    val result = Await.result(database.run(action), WAIT_TIME).head
    GameField(
      gameState = GameState(result._1._2._2, result._1._2._3, Player.fromString(result._1._2._4)),
      map = Json.parse(result._2._2).as[GameField].map
    )

  def delete(): Unit =
    val gameFieldDelete = gameFieldTable.delete
    database.run(gameFieldDelete)
    println("Database deleted")





