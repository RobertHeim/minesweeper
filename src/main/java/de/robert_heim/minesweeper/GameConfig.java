package de.robert_heim.minesweeper;

public class GameConfig {
  public int cols;
  
  public int rows;
  
  public int mines;
  
  public GameConfig(int cols, int rows, int mines) {
    this.cols = cols;
    this.rows = rows;
    this.mines = mines;
  }
  
  public static GameConfig beginner() {
    return new GameConfig(7, 9, 9);
  }
  
  public static GameConfig intermediate() {
    return new GameConfig(20, 15, 40);
  }
  
  public static GameConfig expert() {
    return new GameConfig(40, 20, 99);
  }
  
}
