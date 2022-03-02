# Minesweeper

#### This project is an implementation of the game Minesweeper in java
#### The source code can be made public at request.

## Gameplay

Minesweeper is a game involving a grid of cells.  
There are a number of mines randomly placed under certain cells.  
The user can reveal a cell by left-clicking.  
If the cell contains a mine, the user loses the game.  
Otherwise, the cell will be labeled with the number of adjacent mines.
If the revealed cell has no adjacent mines, it will have no label, and will "flood" in
each direction, revealing adjacent mines.  
The flooding will continue until it reaches a cell with an adjacent mine.  
If a user suspects a mine is under an un-revealed cell, they can place a flag
  on it by right-clicking.  
The user wins the game by revealing all cells without clicking any mines.  

## Win / Loss Examples

<p align="center"> 
  <img width="270" height="324" src="https://github.com/olivervz/minesweeper/blob/master/win.gif">
  <img width="270" height="324" src="https://github.com/olivervz/minesweeper/blob/master/lose.gif">
</p>

