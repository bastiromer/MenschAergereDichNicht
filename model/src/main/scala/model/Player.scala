package model

object Player:
  case object Red extends Player("Red")
  case object Blue extends Player("Blue")
  case object Yellow extends Player("Yellow")
  case object Green extends Player("Green")

  def fromString(str: String): Player =
    val firstChar = str.toLowerCase.headOption.getOrElse(' ')
    firstChar match {
      case 'r' => Red
      case 'b' => Blue
      case 'y' => Yellow
      case 'g' => Green
      case _ => throw new IllegalArgumentException("Invalid player initial")
    }


sealed abstract class Player(val color: String):
  def next: Player =
    this match {
      case Player.Green => Player.Red
      case Player.Red => Player.Blue
      case Player.Blue => Player.Yellow
      case Player.Yellow => Player.Red
    }

  def firstCellIndex: Int =
    this match {
      case Player.Green => 20
      case Player.Red => 30
      case Player.Yellow => 50
      case Player.Blue => 40
    }

  def lastCellIndex(): Int =
    this match {
      case Player.Green => 59
      case Player.Red => 29
      case Player.Yellow => 49
      case Player.Blue => 39
    }

  def endCellIndexes(): List[Int] =
    this match {
      case Player.Green => List(70, 71, 72, 73)
      case Player.Red => List(74, 75, 76, 77)
      case Player.Yellow => List(82, 83, 84, 85)
      case Player.Blue => List(78, 79, 80, 81)
    }
