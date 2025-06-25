package Persistence.DB.slick

import model.Player
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.ForeignKeyQuery
import spray.json.*

import scala.annotation.targetName

class GameStateTable(tag: Tag) extends Table[(Option[Int], Boolean, Int, String)](tag, "GAME_STATE") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def shouldDice = column[Boolean]("SHOULD_DICE")
  def diceNumber = column[Int]("DICE_NUMBER")
  def currentPlayer = column[String]("CURRENT_PLAYER")

  def * = (id, shouldDice, diceNumber, currentPlayer)
}
