package model

case class Token(player: Player, number: Int):
  override def toString: String =
    player.color(0) + number.toString

  def playerHouseIndex: Int =
    player match {
      case Player.Green => -1 + number
      case Player.Red => 3 + number
      case Player.Yellow => 7 + number
      case Player.Blue => 11 + number
    }
