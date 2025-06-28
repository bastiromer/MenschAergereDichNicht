# MenschÄrgereDichNicht

## How it works:
- The goal is to move all your tokens from the starting area, around the board, and into your "home" zone.
- Players roll a die to move.
- Landing on an opponent's token sends it back to its start.
- Only one token can occupy a space—so blocking and sending others back is part of the strategy.

### Commands
Commands for game controll:

- `undo`:           Undo the last move.
- `redo`:           Redo the last undone move.
- `dice`:           Roll the dice.
- `move`:           show a list with all possible moves from which you can choose one.
- `load <target>`:    Load a game from files.
- `save <filename>`:  Save the current game.
- `newGame`:        Start a new game.

### Start the project with Docker
- sbt docker:publishLocal
- docker compose up
- docker attach ui-service
