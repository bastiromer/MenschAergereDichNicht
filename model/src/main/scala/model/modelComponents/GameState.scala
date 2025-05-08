package model.modelComponents

import scala.util.Random

case class GameState(shouldDice: Boolean, diceNumber: Int, currentPlayer: Player):
  def rollDice(map: Map[(Int, Int), Cell]): GameState =
    val newDiceNumber = dice//Random.nextInt(6) + 1
    if (GameField(map, this.copy(diceNumber = newDiceNumber)).possibleMoves().isEmpty) {
      this.copy(shouldDice = true, diceNumber = newDiceNumber, currentPlayer = currentPlayer.next)
    } else {
      this.copy(shouldDice = false, diceNumber = newDiceNumber)
    }

  def dice: Int =
    Random.nextInt(6) + 1

  def computeMove(player: Player): GameState =
    if (player != currentPlayer) {
      this
    } else if (diceNumber == 6) {
      this.copy(shouldDice = true)
    } else {
      this.copy(shouldDice = true, currentPlayer = currentPlayer.next)
    }

  override def toString: String =
    s"Dice: $diceNumber\n" + s"${currentPlayer.toString}" + s"${if shouldDice then " have to dice" else " have to move"}"

