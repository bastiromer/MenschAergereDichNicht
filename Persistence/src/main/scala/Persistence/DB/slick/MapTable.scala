package Persistence.DB.slick

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.ForeignKeyQuery

class MapTable(tag: Tag) extends Table[(Option[Int], String)](tag, "MAP") {
  def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
  def map = column[String]("MAP_JSON")

  def * = (id, map)
}
