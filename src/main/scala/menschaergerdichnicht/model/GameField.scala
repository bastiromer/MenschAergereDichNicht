package menschaergerdichnicht.model

import menschaergerdichnicht.util.commonFunctions.gameFieldInit


case class GameField(map: Map[(Int, Int), Cell], gameState: GameState):
  def move(move: Move): GameField =
    GameField(
      map.map { case ((x, y), cell) =>
        if (cell.index == move.fromIndex) (x, y) ->
          cell.copy(token = None)
        else if (cell.index == move.toIndex) (x, y) ->
          cell.copy(token = map.values.find(cell => cell.index == move.fromIndex).get.token)
        else (x, y) ->
          cell
      },
      gameState.computeMove(
        map.values.find(cell => cell.index == move.fromIndex).get.token.get.player
      )
    )

  def rollDice: GameField = this.copy(gameState = gameState.rollDice(map))

  override def toString: String =
    map.keys.toList.sortBy(key => (key._2, key._1))
      .map(key => map(key).toString)
      .grouped(map.keys.map(_._1).max + 1)
      .map(_.mkString(""))
      .mkString("\n") + "\n" +
      gameState.toString

object GameField:
  def init(): GameField = gameFieldInit()