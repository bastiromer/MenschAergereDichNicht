package model

import model.{Cell, GameField, GameState, Move, Player}

extension (gameField: GameField)
  def possibleMoves(): List[Move] =
    gameField.map.values
      .filter(cell => hasTokenFromCurrentPlayer(cell, gameField.gameState.currentPlayer))
      .filter(cell => moveOffsetIsLegal(cell))
      .map(cell => mapToMove(cell, gameField.gameState) )
      .toList

  private def hasTokenFromCurrentPlayer(cell: Cell, currentPlayer: Player): Boolean =
    cell.token match {
      case Some(token) => token.player == currentPlayer
      case None => false
    }

  private def moveOffsetIsLegal(cell: Cell): Boolean =
    if (cell.index < 20) {
      if (gameField.gameState.diceNumber != 6) return false
      gameField.map.values.find(cell =>
        cell.index == gameField.gameState.currentPlayer.firstCellIndex
      ).get.token match {
        case Some(token) => token.player != gameField.gameState.currentPlayer
        case None => true
      }
    }
    else {
      add(gameField.gameState.currentPlayer, cell.index, gameField.gameState.diceNumber) match {
        case None => false
        case Some(newIndex) => gameField.map.find(cell =>
          cell._2.index == newIndex).get._2.token match {
          case Some(token) => token.player == gameField.gameState.currentPlayer
          case None => true
        }
      }
    }

  private def mapToMove(cell: Cell, gameState: GameState): Move =
    if (cell.index < 20)
      Move(
        fromIndex = cell.index,
        toIndex = gameState.currentPlayer.firstCellIndex
      )
    else
      Move(
        fromIndex = cell.index,
        toIndex = add(gameState.currentPlayer, cell.index, gameState.diceNumber).get
      )

  private def add(player: Player, from: Int, dice: Int): Option[Int] =
    if (player.endCellIndexes().contains(from))
      Some(from + dice).filter(player.endCellIndexes().contains)
    else if (goOverEnd(player, from, dice))
      if (from + dice - player.lastCellIndex() <= player.endCellIndexes().size)
        Some(player.endCellIndexes()(from + dice - player.lastCellIndex() - 1))
      else
        None
    else if (from + dice > Player.Green.lastCellIndex())
      Some(from + dice - 40)
    else
      Some(from + dice)

  private def goOverEnd(player: Player, from: Int, dice: Int): Boolean =
    player match {
      case Player.Green => from + dice > player.lastCellIndex()
      case _ => player.lastCellIndex() >= from && player.lastCellIndex() < from + dice
    }
