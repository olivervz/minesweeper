import java.util.*;
import javalib.impworld.*;
import tester.Tester;
import java.awt.Color;
import javalib.worldimages.*;

//PLEASE NOTE:
//This file has the completed functionality and tests for both part 1
//and part 2 of the assignment, if only grading part 1 functionality, please
//ignore thepart 2 functionality, as we will resubmit this on handin for part 2
//when it opens up

// Represent the game board containing all cells
class Board extends World {
  ArrayList<Cell> cells; // arraylist of the cells on the board
  int width; // number of cells width
  int height; // number of cells height
  int mines; // number of mines
  int cellSize; // cell size in pixels
  int isOver; // 0 if not over, 1 if won, 2 if lost // 0 if not over, 1 if won, 2 if lost

  // random object to add mines
  Random rand = new Random();

  // initialize all fields
  Board(int width, int height, int mines) {
    this.width = width;
    this.height = height;
    this.mines = mines;
    this.cellSize = 30;
    this.isOver = 0;
    // initialize the board
    boardInit();
  }

  // initialize the board with all cells unclicked
  // initialize the locations of mines
  public void boardInit() {
    // initialize the arraylist
    this.cells = new ArrayList<Cell>(this.width * this.height);

    // ArrayList containing location of mines
    ArrayList<Integer> mineLocations = new ArrayList<Integer>(this.mines);

    // all possible indices
    ArrayList<Integer> possibleLocations = new ArrayList<Integer>(this.width * this.height);

    // Add indices to arraylist
    for (int i = 0; i < this.width * this.height; i++) {
      possibleLocations.add(i);
    }

    // Choose indices for mines
    for (int i = 0; i < this.mines; i++) {
      // chose index for mine
      int index = rand.nextInt(this.width * this.height - i);
      // add index to mine location ArrayList
      mineLocations.add(possibleLocations.get(index));
      // remove index from pool of possible indices
      possibleLocations.remove(index);
    }

    // create all cells
    for (int i = 0; i < this.width * this.height; i++) {
      // if selected as a mine location, initialize as mine
      if (mineLocations.contains(i)) {
        this.cells.add(new Cell(true));
      }
      else {
        this.cells.add(new Cell(false));
      }
    }

    // generate neighbors for all cells
    for (int i = 0; i < cells.size(); i++) {

      // NW corner
      if (i == 0) {
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width + 1));
      }
      // NE corner
      else if (i == this.width - 1) {
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width - 1));
      }
      // SW corner
      else if (i == (this.width * (this.height - 1))) {
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width + 1));
      }
      // SE corner
      else if (i == ((this.width * this.height) - 1)) {
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width - 1));
      }

      // TOP border
      else if (i > 0 && i < this.width - 1) {
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width + 1));
        cells.get(i).neighbors.add(cells.get(i + this.width - 1));
      }

      // BOTTOM border
      else if (i < (this.width * this.height - 1) 
          && i > (this.width * (this.height - 1))) {
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width + 1));
        cells.get(i).neighbors.add(cells.get(i - this.width - 1));
      }

      // LEFT border
      else if (i % this.width == 0) {
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width + 1));
        cells.get(i).neighbors.add(cells.get(i + this.width + 1));
      }

      // RIGHT border
      else if ((i + 1) % this.width == 0) {
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width - 1));
        cells.get(i).neighbors.add(cells.get(i + this.width - 1));
      }
      // OTHER piece
      else {
        cells.get(i).neighbors.add(cells.get(i + 1));
        cells.get(i).neighbors.add(cells.get(i - 1));
        cells.get(i).neighbors.add(cells.get(i + this.width));
        cells.get(i).neighbors.add(cells.get(i - this.width));
        cells.get(i).neighbors.add(cells.get(i + this.width + 1));
        cells.get(i).neighbors.add(cells.get(i - this.width - 1));
        cells.get(i).neighbors.add(cells.get(i + this.width - 1));
        cells.get(i).neighbors.add(cells.get(i - this.width + 1));
      }
    }
  }

  // return the number of currently flagged cells
  public int numFlagged() {
    int numFlagged = 0;
    // iterate through all cells, return number of flagged cells
    for (Cell cell : this.cells) {
      if (cell.isFlagged) {
        numFlagged ++;
      }
    }
    return numFlagged;
  }

  // initialize the scene
  public WorldScene makeScene() {
    WorldScene canvas = this.getEmptyScene();
    this.draw(canvas);
    return canvas;
  }

  // draw all cells in the scene
  public void draw(WorldScene canvas) {
    // Game over, either won or lost
    if (this.isOver > 0) {
      // iterate through all cells
      for (Cell cell : this.cells) {
        // if flagged correctly, don't reveal
        // if flagged incorrectly, or unflagged mine, reveal
        if ((cell.isMine || cell.isFlagged) && !(cell.isMine && cell.isFlagged)) {
          cell.isClicked = true;
        }
      }
      if (this.isOver == 2) {
        canvas.placeImageXY(new TextImage("YOU LOSE", 20, Color.BLACK),
            canvas.width / 2, canvas.height - 15);
      }
      else {
        canvas.placeImageXY(new TextImage("YOU WIN", 20, Color.BLACK),
            canvas.width / 2, canvas.height - 15);
      }
    }
    // iterate through all mines
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        // create a WorldImage for each cell in the ArrayList
        WorldImage cell = this.cells.get(i * this.width + j).drawCell(this.cellSize);
        // create an outline for each cell
        WorldImage outlineCell = new FrameImage(cell);
        // place the image at its position on the board
        canvas.placeImageXY(outlineCell, j * cellSize + cellSize / 2,
            i * cellSize + cellSize / 2);
      }
    }
    // show the user how many mines are remaining (assuming all flags placed correctly)
    canvas.placeImageXY(new TextImage("Mines Remaining: " + Integer.toString(
        this.mines - this.numFlagged()), 15, Color.BLACK),
        canvas.width / 2, canvas.height - 35);
  }

  // on mouse click (right or left click)
  public void onMouseClicked(Posn pos, String button) {
    //System.out.println(pos);
    // determine the cell clicked on based on the position
    int col = pos.x / this.cellSize;
    int row = pos.y / this.cellSize;
    int cell = row * this.width + col;

    // if game isn't over, allow additional mouse clicks
    if (this.isOver == 0) {
      if (button.equals("RightButton")) {
        // create flag, or remove flag based on whether the cell is currently flagged
        // only allow for a flag to be placed on an unclicked cell
        if (!this.cells.get(cell).isClicked) {
          if (this.cells.get(cell).isFlagged) {
            this.cells.get(cell).isFlagged = false;
          }
          else {
            this.cells.get(cell).isFlagged = true;
          }
        }
      }

      if (button.equals("LeftButton")) {
        // left clicks on flagged cells does nothing
        if (!this.cells.get(cell).isFlagged) {
          // check if mine
          if (this.cells.get(cell).isMine) {
            // explode the mine
            this.cells.get(cell).isExploded = true;
            // user lost the game
            this.isOver = 2;
          }

          // if not a mine, flood neighboring cells
          else {
            this.cells.get(cell).floodCells();
          }
        }
      }

      // Win condition, number of cells clicked matches number of non-mine cells
      int numClicked = 0;
      for (Cell c : this.cells) {
        if (c.isClicked) {
          numClicked ++;
        }
      }
      if (this.cells.size() - numClicked == this.mines) {
        // user won the game
        this.isOver = 1;
      }
    }
  }
}

// Represent an individual cell on the game board
class Cell {
  boolean isMine; // true if mine
  boolean isClicked; // true if clicked (revealed)
  boolean isFlagged; // true if flagged
  boolean isExploded; // true when a mine is clicked, ends the game
  ArrayList<Cell> neighbors; // list of all neighboring cells
  ArrayList<Color> colors = new ArrayList<Color>(
      Arrays.asList(Color.BLUE, Color.GREEN, Color.RED, Color.BLACK)); // get color by index

  // initialize a cell as either a mine or tile
  Cell(boolean isMine) {
    this.isMine = isMine;
    this.isClicked = false;
    this.isFlagged = false;
    this.isExploded = false;
    this.neighbors = new ArrayList<Cell>();
  }

  // recursively flood all neighboring cells
  public void floodCells() {
    // only flood if not already revealed and unflagged
    if (!this.isClicked && !this.isMine) {
      // reveal current cell
      this.isClicked = true;
      // only recursively call if no adjacent mines
      if (this.numNeighborMines() == 0) {
        for (Cell cell: neighbors) {
          // if cell is flagged, do not flood regardless of mine
          if (!cell.isFlagged) {
            if (cell.numNeighborMines() == 0) {
              // recursively flood cells
              cell.floodCells();
            }
            // if neighboring cell has neighboring mines, reveal, no recursive call
            else {
              cell.isClicked = true;
            }
          }
        }
      }
    }
  }

  // return the number of neighboring mines
  public int numNeighborMines() {
    int totalMines = 0;
    // iterate through all neighboring cells
    for (Cell cell : this.neighbors) {
      if (cell.isMine) {
        totalMines += 1;
      }
    }
    return totalMines;
  }

  // draw an individual cell
  public WorldImage drawCell(int cellSize) {
    // return cell
    WorldImage finalCell;
    WorldImage clickedCell = new RectangleImage(cellSize, cellSize,
        OutlineMode.SOLID, Color.LIGHT_GRAY);
    WorldImage unclickedCell = new RectangleImage(cellSize, cellSize,
        OutlineMode.SOLID, Color.DARK_GRAY);
    // user clicks on mine, exploded cell, red
    WorldImage explodedCell = new RectangleImage(cellSize, cellSize,
        OutlineMode.SOLID, Color.RED);
    WorldImage mine = new CircleImage(cellSize / 3, OutlineMode.SOLID, Color.BLACK);
    WorldImage flag = new EquilateralTriangleImage(cellSize * 2 / 3,
        OutlineMode.SOLID, Color.ORANGE);
    WorldImage xCross1 = new LineImage(new Posn(cellSize * 2 / 3,
        cellSize * 2 / 3), Color.RED);
    WorldImage xCross2 = new RotateImage(xCross1, 90.0);
    WorldImage xCross = new OverlayImage(xCross1, xCross2);

    // exploded, make red, show mine
    if (this.isExploded) {
      finalCell = new OverlayImage(mine, explodedCell);
    }
    // reveal the cell
    else if (this.isClicked) {
      // reveal a mine
      if (this.isMine) {
        finalCell = new OverlayImage(mine, clickedCell);
      }
      // game over condition, check if flags are wrong
      else if (this.isFlagged && !this.isMine) {
        finalCell = new OverlayImage(mine, clickedCell);
        finalCell = new OverlayImage(xCross, finalCell);
      }
      // reveal a cell
      else if (this.numNeighborMines() > 0) {
        finalCell = new OverlayImage(new TextImage(Integer.toString(this.numNeighborMines()),
            this.colors.get(this.numNeighborMines() - 1)), clickedCell);
      }
      // otherwise reveal cell
      else {
        finalCell = clickedCell;
      }
    }
    // cell is unclicked
    else {
      // flag an unclicked cell
      if (this.isFlagged) {
        finalCell = new OverlayImage(flag, unclickedCell);
      }
      // otherwise cell remains unclicked
      else {
        finalCell = unclickedCell;
      }
    }
    return finalCell;
  }
}

class ExamplesMinesweeper {
  Board b;
  Board b1;
  Board b2;
  Board b3;
  Board b4;
  Cell c;
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;

  public void initExamples() {
    b = new Board(5, 5, 0);
    b1 = new Board(10, 10, 5);
    b2 = new Board(20, 20, 15);
    b3 = new Board(30, 16, 10);
    b4 = new Board(2, 2, 0);
    // c is not a mine, unclicked
    c = new Cell(false);
    // c1 is a mine, unclicked
    c1 = new Cell(true);
    // c2 is not a mine, clicked
    c2 = new Cell(false);
    c2.isClicked = true;
    // c3 is not a mine, unclicked, flagged
    c3 = new Cell(false);
    c3.isFlagged = true;
    // c4 is a mine, unclicked, flagged
    c4 = new Cell(true);
    c4.isFlagged = true;
    // c5 is a mine, clicked, exploded
    c5 = new Cell(true);
    c5.isClicked = true;
    c5.isExploded = true;
    // c6 is a mine, clicked, not exploded
    c6 = new Cell(true);
    c6.isClicked = true;
    // c7 is not a mine, clicked, flagged
    c7 = new Cell(false);
    c7.isClicked = true;
    c7.isFlagged = true;
  }

  // Tests for Board
  // tests for boardInit()
  public void testboardInit(Tester t) {
    this.initExamples();
    b.boardInit();
    t.checkExpect(this.b.height, 5);
    t.checkExpect(this.b1.width, 10);
    t.checkExpect(this.b2.mines, 15);
    t.checkExpect(b3.cells.size(), 30 * 16);
  }

  // tests for numFlagged()
  public void testNumFlagged(Tester t) {
    this.initExamples();
    t.checkExpect(this.b.numFlagged(), 0);
    // add flag to cell
    this.b.cells.get(0).isFlagged = true;
    t.checkExpect(this.b.numFlagged(), 1);
    // flooding a flagged cell doesn't work, should remain flagged
    this.b.cells.get(0).floodCells();
    t.checkExpect(this.b.numFlagged(), 1);
    // remove flag from cell
    this.b.cells.get(0).isFlagged = false;
    t.checkExpect(this.b.numFlagged(), 0);
  }

  // tests for makeScene()
  public void testMakeScene(Tester t) {
    this.initExamples(); 
    WorldScene canvas = b.getEmptyScene();
    this.b.draw(canvas);
    t.checkExpect(this.b.makeScene(), canvas);
  }

  // tests for draw()
  public void testDraw(Tester t) {
    this.initExamples();
    WorldScene canvas = b4.getEmptyScene();
    WorldScene canvas1 = b4.getEmptyScene();
    WorldImage cell1 = b4.cells.get(0 * b4.width + 0).drawCell(b4.cellSize);
    WorldImage outlineCell1 = new FrameImage(cell1);
    canvas.placeImageXY(outlineCell1, 0 * b4.cellSize + b4.cellSize / 2,
        0 * b4.cellSize + b4.cellSize / 2);
    WorldImage cell2 = b4.cells.get(1 * b4.width + 0).drawCell(b4.cellSize);
    WorldImage outlineCell2 = new FrameImage(cell2);
    canvas.placeImageXY(outlineCell2, 0 * b4.cellSize + b4.cellSize / 2,
        1 * b4.cellSize + b4.cellSize / 2);
    WorldImage cell3 = b4.cells.get(0 * b4.width + 1).drawCell(b4.cellSize);
    WorldImage outlineCell3 = new FrameImage(cell3);
    canvas.placeImageXY(outlineCell3, 1 * b4.cellSize + b4.cellSize / 2,
        0 * b4.cellSize + b4.cellSize / 2);
    WorldImage cell4 = b4.cells.get(1 * b4.width + 1).drawCell(b4.cellSize);
    WorldImage outlineCell4 = new FrameImage(cell4);
    canvas.placeImageXY(outlineCell4, 1 * b4.cellSize + b4.cellSize / 2,
        1 * b4.cellSize + b4.cellSize / 2);
    canvas.placeImageXY(new TextImage("Mines Remaining: 0", 15, Color.BLACK),
        canvas.width / 2, canvas.height - 35);
    b4.draw(canvas1);
    t.checkExpect(canvas1, canvas);
  }

  // tests for onMouseClicked()
  public void testOnMouseClicked(Tester t) {
    this.initExamples();
    Board bcopy = this.b;
    b.onMouseClicked(new Posn(0, 0), "LeftButton");
    bcopy.cells.get(0).floodCells();
    // game is over becuase no mines, all flooded
    bcopy.isOver = 1;
    t.checkExpect(b, bcopy);
    
    this.initExamples();
    bcopy = this.b3;
    // flag the 15th cell
    b3.onMouseClicked(new Posn(464, 16), "RightButton");
    bcopy.cells.get(15).isFlagged = true;
    t.checkExpect(b3, bcopy);
    // shouldn't flood since it is flagged
    b3.onMouseClicked(new Posn(464, 16), "LeftButton");
    t.checkExpect(b3, bcopy);
    // unflag the 15th cell
    b3.onMouseClicked(new Posn(464, 16), "RightButton");
    bcopy.cells.get(15).isFlagged = false;
    t.checkExpect(b3, bcopy);
    // flood the 15th cell
    b3.onMouseClicked(new Posn(464, 16), "LeftButton");
    bcopy.cells.get(15).floodCells();
    t.checkExpect(b3, bcopy);
  }

  // Tests for Cell
  // tests for testNumNeighborMines()
  public void testNumNeighborMines(Tester t) {
    this.initExamples();
    //cell with no neighboring mines should return 0
    t.checkExpect(this.b.cells.get(0).numNeighborMines(), 0);
    this.b.cells.get(0).isMine = true;
    //changing one neighbor to be a mine cell should increase count by 1
    t.checkExpect(this.b.cells.get(1).numNeighborMines(), 1);
    //calling the function on a mine should not add that tile to the number
    //of neighbor mines
    t.checkExpect(this.b.cells.get(0).numNeighborMines(), 0);
  }

  // tests for floodCells()
  public void testFloodCells(Tester t) {
    this.initExamples();
    b.cells.get(0).floodCells();
    int numClicked = 0;
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    // should fully flood for no mines
    t.checkExpect(numClicked, b.cells.size());  

    this.initExamples();
    numClicked = 0;
    b.cells.get(0).isMine = true;
    b.cells.get(0).floodCells();
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    // shouldn't flood at all
    t.checkExpect(numClicked, 0);

    this.initExamples();
    numClicked = 0;
    b.cells.get(1).isMine = true;
    b.cells.get(0).floodCells();
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    // shouldn't flood outside of the 0th cell
    t.checkExpect(numClicked, 1);

    this.initExamples();
    numClicked = 0;
    // create a horizontal line of mines, flooding should not pass through
    b.cells.get(15).isMine = true;
    b.cells.get(16).isMine = true;
    b.cells.get(17).isMine = true;
    b.cells.get(18).isMine = true;
    b.cells.get(19).isMine = true;
    b.cells.get(0).floodCells();
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    // Should flood up until the line of mines, 5 + 5 + 5 = 15 cells flooded
    t.checkExpect(numClicked, 15);
    
    this.initExamples();
    numClicked = 0;
    // should flood through diagonal line of flagged cells
    b.cells.get(4).isFlagged = true;
    b.cells.get(8).isFlagged = true;
    b.cells.get(12).isFlagged = true;
    b.cells.get(16).isFlagged = true;
    b.cells.get(20).isFlagged = true;
    b.cells.get(0).floodCells();
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    t.checkExpect(numClicked, 20);

    this.initExamples();
    numClicked = 0;
    // shouldn't flood through vertical line of flagged cells
    b.cells.get(2).isFlagged = true;
    b.cells.get(7).isFlagged = true;
    b.cells.get(12).isFlagged = true;
    b.cells.get(17).isFlagged = true;
    b.cells.get(22).isFlagged = true;
    b.cells.get(0).floodCells();
    for (Cell cell: b.cells) {
      if (cell.isClicked) {
        numClicked ++;
      }
    }
    t.checkExpect(numClicked, 10);
  }

  // tests for drawCell()
  public void testDrawCell(Tester t) {
    int cellSize = 30;
    WorldImage mine = new CircleImage(cellSize / 3, OutlineMode.SOLID,
        Color.BLACK);
    WorldImage flag = new EquilateralTriangleImage(cellSize * 2 / 3,
        OutlineMode.SOLID, Color.ORANGE);
    WorldImage xCross1 = new LineImage(new Posn(cellSize * 2 / 3,
        cellSize * 2 / 3), Color.RED);
    WorldImage xCross2 = new RotateImage(xCross1, 90.0);
    WorldImage xCross = new OverlayImage(xCross1, xCross2);
    this.initExamples();

    // test that all combinations of cells can be drawn
    t.checkExpect(c.drawCell(cellSize), new RectangleImage(cellSize,
        cellSize, OutlineMode.SOLID, Color.DARK_GRAY));
    t.checkExpect(c1.drawCell(cellSize), new RectangleImage(cellSize,
        cellSize, OutlineMode.SOLID, Color.DARK_GRAY));
    t.checkExpect(c2.drawCell(cellSize), new RectangleImage(cellSize,
        cellSize, OutlineMode.SOLID, Color.LIGHT_GRAY));
    t.checkExpect(c3.drawCell(cellSize), new OverlayImage(flag, new
        RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.DARK_GRAY)));
    t.checkExpect(c4.drawCell(cellSize), new OverlayImage(flag, new
        RectangleImage(cellSize, cellSize, OutlineMode.SOLID, Color.DARK_GRAY)));
    t.checkExpect(c5.drawCell(cellSize), new OverlayImage(mine, new
        RectangleImage(cellSize, cellSize, OutlineMode.SOLID,
        Color.RED)));
    t.checkExpect(c6.drawCell(cellSize), new OverlayImage(mine, new
        RectangleImage(cellSize, cellSize, OutlineMode.SOLID,
        Color.LIGHT_GRAY)));
    t.checkExpect(c7.drawCell(cellSize), new OverlayImage(xCross, new
        OverlayImage(mine, new RectangleImage(cellSize, cellSize,
        OutlineMode.SOLID, Color.LIGHT_GRAY))));
  }

  // Test game board to make sure that all types of cells and shapes can be drawn
  public void testGameIntermediate(Tester t) {
    this.initExamples();
    b1.cells.get(0).floodCells();
    b1.cells.get(b1.width * b1.height - 1).floodCells();
    for (Cell cell : b1.cells) {
      if (cell.isMine) {
        cell.isFlagged = true;
      }
    }
    b1.bigBang(b1.width * b1.cellSize, b1.height * b1.cellSize + 50);
  }

  // Runs the game with bigBang
  public void testGame(Tester t) {
    this.initExamples();
    b.bigBang(b.width * b.cellSize, b.height * b.cellSize + 50);
    b1.bigBang(b1.width * b1.cellSize, b1.height * b1.cellSize + 50);
    b2.bigBang(b2.width * b2.cellSize, b2.height * b2.cellSize + 50);
    b3.bigBang(b3.width * b3.cellSize, b3.height * b3.cellSize + 50);
  }
}
