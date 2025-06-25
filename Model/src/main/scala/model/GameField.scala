package model

import model.Player.{Blue, Green, Red, Yellow}

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
  def init(): GameField =
    GameField((for {
      x <- 0 to 10
      y <- 0 to 10
    } yield {
      val cellValue = (x, y) match {
        case (0, 0) => Cell(isAPlayField = true, index = 0, token = Some(Token(Green, 1)))
        case (2, 0) => Cell(isAPlayField = true, index = 1, token = Some(Token(Green, 2)))
        case (0, 2) => Cell(isAPlayField = true, index = 2, token = Some(Token(Green, 3)))
        case (2, 2) => Cell(isAPlayField = true, index = 3, token = Some(Token(Green, 4)))

        case (8, 0) => Cell(isAPlayField = true, index = 4, token = Some(Token(Red, 1)))
        case (10, 0) => Cell(isAPlayField = true, index = 5, token = Some(Token(Red, 2)))
        case (8, 2) => Cell(isAPlayField = true, index = 6, token = Some(Token(Red, 3)))
        case (10, 2) => Cell(isAPlayField = true, index = 7, token = Some(Token(Red, 4)))

        case (0, 8) => Cell(isAPlayField = true, index = 8, token = Some(Token(Yellow, 1)))
        case (2, 8) => Cell(isAPlayField = true, index = 9, token = Some(Token(Yellow, 2)))
        case (0, 10) => Cell(isAPlayField = true, index = 10, token = Some(Token(Yellow, 3)))
        case (2, 10) => Cell(isAPlayField = true, index = 11, token = Some(Token(Yellow, 4)))

        case (8, 8) => Cell(isAPlayField = true, index = 12, token = Some(Token(Blue, 1)))
        case (10, 8) => Cell(isAPlayField = true, index = 13, token = Some(Token(Blue, 2)))
        case (8, 10) => Cell(isAPlayField = true, index = 14, token = Some(Token(Blue, 3)))
        case (10, 10) => Cell(isAPlayField = true, index = 15, token = Some(Token(Blue, 4)))

        case (4, 0) => Cell(isAPlayField = true, index = 28, token = None)
        case (5, 0) => Cell(isAPlayField = true, index = 29, token = None)
        case (6, 0) => Cell(isAPlayField = true, index = 30, token = None)

        case (4, 1) => Cell(isAPlayField = true, index = 27, token = None)
        case (5, 1) => Cell(isAPlayField = true, index = 74, token = None)
        case (6, 1) => Cell(isAPlayField = true, index = 31, token = None)

        case (4, 2) => Cell(isAPlayField = true, index = 26, token = None)
        case (5, 2) => Cell(isAPlayField = true, index = 75, token = None)
        case (6, 2) => Cell(isAPlayField = true, index = 32, token = None)

        case (4, 3) => Cell(isAPlayField = true, index = 25, token = None)
        case (5, 3) => Cell(isAPlayField = true, index = 76, token = None)
        case (6, 3) => Cell(isAPlayField = true, index = 33, token = None)

        case (0, 4) => Cell(isAPlayField = true, index = 20, token = None)
        case (1, 4) => Cell(isAPlayField = true, index = 21, token = None)
        case (2, 4) => Cell(isAPlayField = true, index = 22, token = None)
        case (3, 4) => Cell(isAPlayField = true, index = 23, token = None)
        case (4, 4) => Cell(isAPlayField = true, index = 24, token = None)
        case (5, 4) => Cell(isAPlayField = true, index = 77, token = None)
        case (6, 4) => Cell(isAPlayField = true, index = 34, token = None)
        case (7, 4) => Cell(isAPlayField = true, index = 35, token = None)
        case (8, 4) => Cell(isAPlayField = true, index = 36, token = None)
        case (9, 4) => Cell(isAPlayField = true, index = 37, token = None)
        case (10, 4) => Cell(isAPlayField = true, index = 38, token = None)

        case (0, 5) => Cell(isAPlayField = true, index = 59, token = None)
        case (1, 5) => Cell(isAPlayField = true, index = 70, token = None)
        case (2, 5) => Cell(isAPlayField = true, index = 71, token = None)
        case (3, 5) => Cell(isAPlayField = true, index = 72, token = None)
        case (4, 5) => Cell(isAPlayField = true, index = 73, token = None)
        case (6, 5) => Cell(isAPlayField = true, index = 81, token = None)
        case (7, 5) => Cell(isAPlayField = true, index = 80, token = None)
        case (8, 5) => Cell(isAPlayField = true, index = 79, token = None)
        case (9, 5) => Cell(isAPlayField = true, index = 78, token = None)
        case (10, 5) => Cell(isAPlayField = true, index = 39, token = None)

        case (0, 6) => Cell(isAPlayField = true, index = 58, token = None)
        case (1, 6) => Cell(isAPlayField = true, index = 57, token = None)
        case (2, 6) => Cell(isAPlayField = true, index = 56, token = None)
        case (3, 6) => Cell(isAPlayField = true, index = 55, token = None)
        case (4, 6) => Cell(isAPlayField = true, index = 54, token = None)
        case (5, 6) => Cell(isAPlayField = true, index = 85, token = None)
        case (6, 6) => Cell(isAPlayField = true, index = 44, token = None)
        case (7, 6) => Cell(isAPlayField = true, index = 43, token = None)
        case (8, 6) => Cell(isAPlayField = true, index = 42, token = None)
        case (9, 6) => Cell(isAPlayField = true, index = 41, token = None)
        case (10, 6) => Cell(isAPlayField = true, index = 40, token = None)

        case (4, 7) => Cell(isAPlayField = true, index = 53, token = None)
        case (5, 7) => Cell(isAPlayField = true, index = 84, token = None)
        case (6, 7) => Cell(isAPlayField = true, index = 45, token = None)

        case (4, 8) => Cell(isAPlayField = true, index = 52, token = None)
        case (5, 8) => Cell(isAPlayField = true, index = 83, token = None)
        case (6, 8) => Cell(isAPlayField = true, index = 46, token = None)

        case (4, 9) => Cell(isAPlayField = true, index = 51, token = None)
        case (5, 9) => Cell(isAPlayField = true, index = 82, token = None)
        case (6, 9) => Cell(isAPlayField = true, index = 47, token = None)

        case (4, 10) => Cell(isAPlayField = true, index = 50, token = None)
        case (5, 10) => Cell(isAPlayField = true, index = 49, token = None)
        case (6, 10) => Cell(isAPlayField = true, index = 48, token = None)

        case _ => Cell(isAPlayField = false, index = -1, token = None)
      }
      (x, y) -> cellValue
    }).toMap, GameState(shouldDice = true, diceNumber = 0, currentPlayer = Green))

/*

+----+      +----+      +----++----++----+      +----+      +----+
| G0 |      | G1 |      | 28 || 29 || 30 |      | R4 |      | R5 |
+----+      +----+      +----++----++----+      +----+      +----+
                      +----++----++----+
                      | 27 || 74 || 31 |
                      +----++----++----+
+----+      +----+      +----++----++----+      +----+      +----+
| G2 |      | G3 |      | 26 || 75 || 32 |      | R6 |      | R7 |
+----+      +----+      +----++----++----+      +----+      +----+
                      +----++----++----+
                      | 25 || 76 || 33 |
                      +----++----++----+
+----++----++----++----++----++----++----++----++----++----++----+
| 20 || 21 || 22 || 23 || 24 || 77 || 34 || 35 || 36 || 37 || 38 |
+----++----++----++----++----++----++----++----++----++----++----+
+----++----++----++----++----+      +----++----++----++----++----+
| 59 || 70 || 71 || 72 || 73 |      | 81 || 80 || 79 || 78 || 39 |
+----++----++----++----++----+      +----++----++----++----++----+
+----++----++----++----++----++----++----++----++----++----++----+
| 58 || 57 || 56 || 55 || 54 || 85 || 44 || 43 || 42 || 41 || 40 |
+----++----++----++----++----++----++----++----++----++----++----+
                      +----++----++----+
                      | 53 || 84 || 45 |
                      +----++----++----+
+----+      +----+      +----++----++----+      +----+      +----+
| Y8 |      | Y9 |      | 52 || 83 || 46 |      | B12|      | B13|
+----+      +----+      +----++----++----+      +----+      +----+
                      +----++----++----+
                      | 51 || 82 || 47 |
                      +----++----++----+
+----+      +----+      +----++----++----+      +----+      +----+
| Y10|      | Y11|      | 50 || 49 || 48 |      | B14|      | B15|
+----+      +----+      +----++----++----+      +----+      +----+

*/