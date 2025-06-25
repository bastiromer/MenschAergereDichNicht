package model

case class Move(fromIndex: Int, toIndex: Int):
  def toCell(map: Map[(Int, Int), Cell]): Cell =
    map.values.find( cell => cell.index == toIndex ).get

