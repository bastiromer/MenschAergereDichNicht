# ToyBrokersLudo

## Start in Docker
- sbt docker:publishLocal
- docker compose up
- docker attach ui-service

## Tui - Controlling the Game via Standard Input

### Introduction

The `Tui` class enables controlling the game via the standard input (stdin) of the console. You can input commands to execute various actions and play the game.

### Using the Commands

The game can be controlled using various commands inputted through the console. Here are the supported commands:

- `undo`: Undo the last move.
- `redo`: Redo the last undone move.
- `dice`: Roll a virtual dice.
- `move`: Display possible moves and allow selecting a move.
- `load`: Load a game state from a file.
- `save`: Save the current game state to a file.
- `newGame`: Start a new game.

## Coveralls
https://coveralls.io/github/Julz124
