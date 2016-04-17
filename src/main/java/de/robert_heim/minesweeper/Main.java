package de.robert_heim.minesweeper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import de.robert_heim.minesweeper.Game.EventHook;

public class Main extends Application {
  
  public static MenuItem menuNew = new MenuItem(Lang.t("New"));
  
  private Stage primaryStage;
  
  private BorderPane rootBorderPane;
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Minesweeper");
    
    rootBorderPane = new BorderPane();
    Scene scene = new Scene(rootBorderPane);
    primaryStage.setScene(scene);
    BorderPane statusBorderPane = new BorderPane();
    Group gameArea = new Group();
    
    rootBorderPane.setCenter(statusBorderPane);
    statusBorderPane.setCenter(gameArea);
    rootBorderPane.setBackground(new Background(new BackgroundFill(
        Color.GAINSBORO, CornerRadii.EMPTY, Insets.EMPTY)));
    
    MenuBar menuBar = new MenuBar();
    
    // Menu
    
    MenuItem menuQuit = new MenuItem(Lang.t("Quit"));
    menuQuit.setOnAction(e -> byebye());
    
    // -- Options --
    MenuItem menuBeginner = new MenuItem(Lang.t("Beginner"));
    menuBeginner.setOnAction(e -> deal(gameArea, GameConfig.beginner()));
    MenuItem menuIntermediate = new MenuItem(Lang.t("Intermediate"));
    menuIntermediate.setOnAction(e -> deal(gameArea, GameConfig.intermediate()));
    MenuItem menuExpert = new MenuItem(Lang.t("Expert"));
    menuExpert.setOnAction(e -> deal(gameArea, GameConfig.expert()));
    
    Menu menuOptions = new Menu(Lang.t("Options"));
    menuOptions.getItems().addAll(menuBeginner, menuIntermediate, menuExpert);
    
    // -- GAME --
    menuNew.setOnAction(e -> deal(gameArea, lastGameConfig.orElse(GameConfig.beginner())));
    Menu menuGame = new Menu(Lang.t("Game"));
    menuGame.getItems().addAll(menuNew, menuOptions, new SeparatorMenuItem(), menuQuit);
    menuBar.getMenus().addAll(menuGame);
    
    rootBorderPane.setTop(menuBar);
    
    // status bar
    
    HBox statusBar = new HBox();
    statusBar.setBackground(new Background(new BackgroundFill(
        Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
    Text durationStatusText = new Text(" " + Lang.t("Time") + ":");
    durationStatusText.setFont(Font.font("Verdana", 20));
    durationStatusText.setFill(Color.RED);
    statusBar.getChildren().add(durationStatusText);
    
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          if (currentGame.isPresent()) {
            durationStatusText.setText(" "
                + String.format(Lang.t("Time"), currentGame.get().calcDuration()));
          }
        });
      }
    }, 0, 1000);
    
    statusBorderPane.setTop(statusBar);
    
    menuNew.fire();
    
    primaryStage.show();
  }
  
  public static void main(String[] args) {
    Main.launch(args);
  }
  
  private Optional<GameConfig> lastGameConfig = Optional.empty();
  
  private Optional<Game> currentGame = Optional.empty();
  
  public void deal(Group parentGroup, GameConfig gameConfig) {
    lastGameConfig = Optional.of(gameConfig);
    double width = (gameConfig.cols + 3) * Field.CARD_WIDTH;
    double height = (gameConfig.rows + 3) * Field.CARD_HEIGHT;
    rootBorderPane.setMinWidth(width);
    rootBorderPane.setMaxWidth(width);
    rootBorderPane.setMinHeight(height);
    rootBorderPane.setMaxWidth(height);
    primaryStage.sizeToScene();
    
    currentGame = Optional.of(new Game(gameConfig, onFinish));
    parentGroup.getChildren().clear();
    currentGame.get().getFieldsList().forEach(f -> parentGroup.getChildren().add(f));
    currentGame.get().start();
  }
  
  private EventHook onFinish = (e) -> {
    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.WINDOW_MODAL);
    Long duration = currentGame.get().calcDuration();
    String recordString = getRecord(e, duration, lastGameConfig.get().type);
    String winStr = e == Game.FINISH_FAIL ? Lang.t("You_lost") : Lang.t("You_won");
    Alert alert = new Alert(AlertType.NONE,
        winStr + "\n\n"
            + String.format(Lang.t("Time"), duration) + "\n"
            + recordString + "\n\n"
            + Lang.t("Again?"),
        ButtonType.OK,
        ButtonType.CLOSE);
    alert.showAndWait()
        .ifPresent(response -> {
          if (response == ButtonType.OK) {
            menuNew.fire();
          }
            else if (response == ButtonType.CLOSE) {
              byebye();
            }
          });
  };
  
  @Override
  public void stop() throws Exception {
    super.stop();
    byebye();
  }
  
  private String getRecord(int e, long duration, GameConfig.Type gameType) {
    // we don't store records for custom games
    if (gameType != GameConfig.Type.CUSTOM) {
      String recordKey = "record_" + gameType;
      String recordTimestampKey = "recordTimestamp_" + gameType;
      Optional<Long> record = DB.getLong(recordKey);
      Optional<Long> recordTimestamp = DB.getLong(recordTimestampKey);
      if (e == Game.FINISH_SUCCESS) {
        // check if new record
        if (!record.isPresent() || duration < record.get()) {
          ZonedDateTime now = ZonedDateTime.now();
          record = Optional.of(duration);
          recordTimestamp = Optional.of(now.toEpochSecond());
          DB.put(recordTimestampKey, recordTimestamp.get());
          DB.put(recordKey, record.get());
        }
      }
      
      if (record.isPresent()) {
        String recordString = String.format(Lang.t("Record"), record.get());
        if (recordTimestamp.isPresent()) {
          LocalDateTime local = LocalDateTime.ofInstant(
              Instant.ofEpochSecond(recordTimestamp.get()),
              ZoneId.systemDefault());
          String dateStr = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss").format(local);
          recordString += "\t" + String.format(Lang.t("Record_Time"), dateStr);
        }
        return recordString;
      }
    }
    return "";
  }
  
  public static void byebye() {
    Platform.exit();
    System.exit(0);
  }
}
