package Persistence.DB

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ForeignKey, ForeignKeyQuery}

import scala.annotation.targetName

class GameFieldTable(tag: Tag) extends Table[(Option[Int], Int, Int)](tag, "GAME_FIELD") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def stateId = column[Int]("STATE_ID")
  def mapId = column[Int]("MAP_ID")

  def * = (id, stateId, mapId)
}

