package de.robert_heim.minesweeper;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Field extends Group {
  public static final double CARD_WIDTH = 30;
  
  public static final double CARD_HEIGHT = 30;
  
  private int x;
  
  private int y;
  
  private boolean flagged = false;
  
  private boolean revealed = false;
  
  private boolean mine = false;
  
  private int touchingMines = 0;
  
  private Text text;

  private Rectangle rectangle;
  
  public Field(int x, int y) {
    this.x = x;
    this.y = y;
    rectangle = new Rectangle();
    rectangle.setWidth(CARD_WIDTH);
    rectangle.setHeight(CARD_HEIGHT);
    rectangle.setFill(Color.GRAY);
    rectangle.setStroke(Color.BLACK);
    
    setLayoutX(x * CARD_WIDTH);
    setLayoutY(y * CARD_HEIGHT);
    getChildren().clear();
    getChildren().add(rectangle);
    
    text = new Text();
    text.setTextAlignment(TextAlignment.CENTER);
    text.setTextOrigin(VPos.CENTER);
    text.setStyle("-fx-font-weight:bold");
    text.setFont(Font.font("Verdana", 20));
    getChildren().add(text);
    
  }
  
  @Override
  protected void layoutChildren() {
    final double w = rectangle.getWidth();
    final double h = rectangle.getHeight();
    
    text.setWrappingWidth(w * 0.9);
    text.setLayoutX(w / 2 - text.getLayoutBounds().getWidth() / 2);
    text.setLayoutY(h / 2);
  }
  
  @Override
  public String toString() {
    return "Field [" + x + " " + y + " " + (mine ? "mine" : "clean")
        + "]";
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public void setFlagged(boolean flagged) {
    this.flagged = flagged;
    if (flagged) {
      rectangle.setFill(Color.ORANGE);
    }
    else {
      rectangle.setFill(Color.GREY);
    }
  }
  
  public boolean isFlagged() {
    return flagged;
  }
  
  public boolean isMine() {
    return mine;
  }
  
  public void setMine(boolean mine) {
    this.mine = mine;
  }
  
  public int getTouchingMines() {
    return touchingMines;
  }
  
  public void setTouchingMines(int touchingMines) {
    this.touchingMines = touchingMines;
  }
  
  public void revealMine() {
    if (mine) {
      text.setText("X");
    }
  }

  public void reveal(Field[][] allFields) {
    if (!revealed) {
      revealed = true;
      if (mine) {
        rectangle.setFill(Color.RED);
        text.setText("X");
      }
      else {
        touchingMines = 0;
        int minX = x > 0 ? x - 1 : x;
        int maxX = x >= allFields.length - 1 ? x : x + 1;
        int minY = y > 0 ? y - 1 : y;
        int maxY = y >= allFields[0].length - 1 ? y : y + 1;
        for (int xCheck = minX; xCheck <= maxX; xCheck++) {
          for (int yCheck = minY; yCheck <= maxY; yCheck++) {
            if (x != xCheck || y != yCheck) {
              if (allFields[xCheck][yCheck].isMine()) {
                touchingMines++;
              }
            }
          }
        }
        // touching == 0 -> reveal touching fields
        if (touchingMines == 0) {
          for (int xCheck = minX; xCheck <= maxX; xCheck++) {
            for (int yCheck = minY; yCheck <= maxY; yCheck++) {
              if (x != xCheck || y != yCheck) {
                allFields[xCheck][yCheck].reveal(allFields);
              }
            }
          }
        }
        text.setText("" + touchingMines);
        switch (touchingMines) {
          case 0:
            text.setVisible(false);
            break;
          case 1:
            text.setFill(Color.BLUE);
            break;
          case 2:
            text.setFill(Color.BLUE);
            break;
          case 3:
            text.setFill(Color.RED);
            break;
          case 4:
            text.setFill(Color.PURPLE);
            break;
          case 5:
            text.setFill(Color.DARKBLUE);
            break;
          case 6:
            text.setFill(Color.LIGHTBLUE);
            break;
          default:
            text.setFill(Color.BLACK);
            break;
        }
        rectangle.setFill(Color.LIGHTGRAY);
      }
    }
  }
  
  public boolean isRevealed() {
    return revealed;
  }
}
