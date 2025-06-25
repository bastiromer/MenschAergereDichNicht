package model

case class Cell(isAPlayField: Boolean, index: Int, token: Option[Token]):
  override def toString: String =
    token match
      case Some(token) => token.toString.take(2).padTo(3, ' ')
      case None => if (isAPlayField) " O " else "   "
