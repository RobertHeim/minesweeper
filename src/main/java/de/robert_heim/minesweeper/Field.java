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
    text.setVisible(false);
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
    text.setText("X");
  }
  
  public int getTouchingMines() {
    return touchingMines;
  }
  
  public void setTouchingMines(int touchingMines) {
    this.touchingMines = touchingMines;
    if (!mine) {
      text.setText("" + touchingMines);
      switch (touchingMines) {
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
    }
  }
  
  public void reveal() {
    if (!revealed) {
      revealed = true;
      if (0 != touchingMines || mine) {
        text.setVisible(true);
      }
      if (mine) {
        rectangle.setFill(Color.RED);
      }
      else {
        rectangle.setFill(Color.LIGHTGRAY);
      }
    }
  }
  
  public boolean isRevealed() {
    return revealed;
  }
}
