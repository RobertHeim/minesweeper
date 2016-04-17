package de.robert_heim.minesweeper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.scene.input.MouseButton;

public class Game {
  
  private int minesLeft = 0;
  
  private Field[][] allFields;
  
  private List<Field> allFieldsList = new ArrayList<>();
  
  private Optional<LocalDateTime> startTime = Optional.empty();
  
  private Optional<LocalDateTime> endTime = Optional.empty();
  
  public Game(GameConfig game, EventHook onFinish) {
    setOnFinish(onFinish);
    createFields(game.cols, game.rows, game.mines);
    minesLeft = game.mines;
  }
  
  public void start() {
    endTime = Optional.empty();
    startTime = Optional.of(LocalDateTime.now());
    finished = false;
  }
  
  public List<Field> getFieldsList() {
    return allFieldsList;
  }
  
  private void createFields(int width, int height, int minesCount) {
    allFields = new Field[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        Field field = new Field(x, y);
        allFields[x][y] = field;
        allFieldsList.add(field);
      }
    }
    // put mines on fields
    List<Field> copy = new ArrayList<>(allFieldsList);
    Collections.shuffle(copy);
    for (int i = 0; i < copy.size() && i < minesCount; i++) {
      copy.get(i).setMine(true);
    }
    calcMinesEnvironmentCount();
    // make fields clickable
    allFieldsList.forEach(f -> {
      f.setOnMouseReleased(e -> {
        if (f.isRevealed()) {
          if (e.isPrimaryButtonDown() && e.getButton() == MouseButton.SECONDARY
              || e.getButton() == MouseButton.PRIMARY && e.isSecondaryButtonDown()) {
            if (f.isRevealed()) {
              revealUnflaggedAdjacentFields(f);
            }
          }
        }
          else {
            if (e.getButton() == MouseButton.SECONDARY) {
              if (!f.isFlagged()) {
                f.setFlagged(true);
                minesLeft--;
              }
              else {
                f.setFlagged(false);
                minesLeft++;
              }
            }
            else {
              if (!f.isFlagged()) {
                reveal(f);
                checkFinish(f.isMine());
              }
            }
          }
          
        });
    });
  }
  
  public interface EventHook {
    void onEvent(int event);
  }
  
  private EventHook onFinish;
  
  public static final int FINISH_SUCCESS = 0;
  
  public static final int FINISH_FAIL = 1;
  
  public void setOnFinish(EventHook onFinish) {
    this.onFinish = onFinish;
  }
  
  boolean finished = false;
  
  public void checkFinish(boolean mineHit) {
    // checkFinish might be called multiple times in some recursion,
    // but we do not want to finish multiple times
    if (!finished) {
      boolean allNonMinesRevealed = !allFieldsList.stream()
          .filter(f -> !f.isRevealed() && !f.isMine())
          .findAny().isPresent();
      if (allNonMinesRevealed || mineHit) {
        endTime = Optional.of(LocalDateTime.now());
        if (mineHit) {
          revealAllMines();
        }
        finished = true;
        onFinish.onEvent(mineHit ? FINISH_FAIL : FINISH_SUCCESS);
      }
    }
  }
  
  private void revealAllMines() {
    allFieldsList.stream().filter(f -> f.isMine()).forEach(f -> f.reveal());
  }
  
  public long calcDuration() {
    Duration duration = (Duration.between(
        startTime.orElse(LocalDateTime.now()),
        endTime.orElse(LocalDateTime.now())));
    return duration.getSeconds();
  }
  
  /**
   * Reveals the given field and in case there are not adjacent mines, it recursively reveals all
   * adjacent fields.
   * 
   * @param field
   */
  private void reveal(Field field) {
    if (!field.isRevealed()) {
      field.reveal();
      if (0 == field.getTouchingMines()) {
        // recursion
        revealUnflaggedAdjacentFields(field);
      }
    }
  }
  
  private void revealUnflaggedAdjacentFields(Field field) {
    int flaggedCount = (int) getAdjacentFields(field).stream().filter(f -> f.isFlagged()).count();
    if (field.getTouchingMines() == flaggedCount) {
      boolean mineHit = false;
      List<Field> env = getAdjacentFields(field);
      for (Field currentField : env) {
        if (!currentField.isFlagged()) {
          // recursion
          reveal(currentField);
          if (currentField.isMine()) {
            mineHit = true;
          }
        }
      }
      checkFinish(mineHit);
    }
  }
  
  private List<Field> getAdjacentFields(Field field) {
    List<Field> environment = new ArrayList<>();
    int x = field.getX();
    int y = field.getY();
    int minX = x > 0 ? x - 1 : x;
    int maxX = x >= allFields.length - 1 ? x : x + 1;
    int minY = y > 0 ? y - 1 : y;
    int maxY = y >= allFields[0].length - 1 ? y : y + 1;
    for (int xCheck = minX; xCheck <= maxX; xCheck++) {
      for (int yCheck = minY; yCheck <= maxY; yCheck++) {
        if (x != xCheck || y != yCheck) {
          environment.add(allFields[xCheck][yCheck]);
        }
      }
    }
    return environment;
  }
  
  private void calcMinesEnvironmentCount() {
    for (Field f : allFieldsList) {
      f.setTouchingMines((int) getAdjacentFields(f).stream()
          .filter(current -> current.isMine())
          .count());
    }
  }
  
}
