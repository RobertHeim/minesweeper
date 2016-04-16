package de.robert_heim.minesweeper;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import de.robert_heim.minesweeper.Game.EventHook;
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

public class Main extends Application {
  
  public static MenuItem menuNew = new MenuItem("New");
  
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
    
    // -- FILE --
    menuNew.setOnAction(e -> deal(gameArea, lastGameConfig.orElse(GameConfig.beginner())));
    
    MenuItem menuQuit = new MenuItem("Quit");
    menuQuit.setOnAction(e -> byebye());
    
    Menu menuFile = new Menu("File");
    menuFile.getItems().addAll(menuNew, new SeparatorMenuItem(), menuQuit);
    
    // -- EDIT --
    MenuItem menuBeginner = new MenuItem("Beginner");
    menuBeginner.setOnAction(e -> deal(gameArea, GameConfig.beginner()));
    MenuItem menuIntermediate = new MenuItem("Intermediate");
    menuIntermediate.setOnAction(e -> deal(gameArea, GameConfig.intermediate()));
    MenuItem menuExpert = new MenuItem("Expert");
    menuExpert.setOnAction(e -> deal(gameArea, GameConfig.expert()));
    
    Menu menuEdit = new Menu("Edit");
    menuEdit.getItems().addAll(menuBeginner, menuIntermediate, menuExpert);
    
    menuBar.getMenus().addAll(menuFile, menuEdit);
    
    rootBorderPane.setTop(menuBar);
    
    // status bar
    
    HBox statusBar = new HBox();
    statusBar.setBackground(new Background(new BackgroundFill(
        Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
    Text durationStatusText = new Text(" Time:");
    durationStatusText.setFont(Font.font("Verdana", 20));
    durationStatusText.setFill(Color.RED);
    statusBar.getChildren().add(durationStatusText);
    
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          if (currentGame.isPresent() && currentGame.get().isRunning()) {
            durationStatusText.setText(" Time: " + currentGame.get().calcDuration());
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
    
    String winStr = e == Game.FINISH_FAIL ? "lost" : "won";
    Alert alert = new Alert(AlertType.NONE,
        "You " + winStr + "!\n\nTime: " + currentGame.get().calcDuration()
            + " seconds\n\nAgain?",
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
  
  public static void byebye() {
    Platform.exit();
    System.exit(0);
  }
}
