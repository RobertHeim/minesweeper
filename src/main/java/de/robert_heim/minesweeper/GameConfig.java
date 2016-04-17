package de.robert_heim.minesweeper;

public class GameConfig {
  final public int cols;
  
  final public int rows;
  
  final public int mines;
  
  final public Type type;
  
  public GameConfig(int cols, int rows, int mines) {
    this(cols, rows, mines, Type.CUSTOM);
  }
  
  private GameConfig(int cols, int rows, int mines, Type type) {
    this.cols = cols;
    this.rows = rows;
    this.mines = mines;
    this.type = type;
  }
  
  public enum Type {
    BEGINNER, INTERMEDIATE, EXPERT, CUSTOM
  }
  
  public static GameConfig beginner() {
    return new GameConfig(9, 9, 10, Type.BEGINNER);
  }
  
  public static GameConfig intermediate() {
    return new GameConfig(16, 16, 40, Type.INTERMEDIATE);
  }
  
  public static GameConfig expert() {
    return new GameConfig(30, 16, 99, Type.EXPERT);
  }
  
}
