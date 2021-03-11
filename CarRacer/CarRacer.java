import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.util.*;

/*
 * Final Project - Car Racer 
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * CarRacer Class
 * Main GUI thread
 */

public class CarRacer extends Application {
   
   /* Window attributes */
   // Stage (window)
   private Stage stage;
   private Scene scene;
   private int sceneWidth = 1000;
   private int sceneHeight = 600;

   /* Race attributes */
   private Race race;
   
   /* Racer constants */
   private final double RACER_START_X = -450;
   private final double RACER_START_Y = 10;
   private final double RACER_START_DEG = 78;

   /* Map Constants */
   private static final String RACE_MAP = "assets/road2.png";
   private static final String RACE_MASK = "assets/road-mask.png";
   
   /* Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /* Method called by launch(args) */
   @Override public void start(Stage stage) {
      this.stage = stage;
      
      stage.setResizable(true);
      stage.setTitle("Car Racer");
      stage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
               System.exit(0);
            }
         });
   
      updateScene(setupMenu());
   }
   
   /* Method to display another scene */
   public void updateScene(Parent root) {
   
      // Create new scene
      scene = new Scene(root, sceneWidth, sceneHeight);
      
      // Start listening to key events
      handleKeys();
      
      // Update scene
      stage.setScene(scene);
      
      // Show stage
      stage.show();
   }
   
   /* Create and return the Menu GUI */
   public Parent setupMenu() {
      // Create new root pane
      VBox root = new VBox();
      
      Button btnSingleplayer = new Button("Singleplayer");
      Button btnMultiplayer = new Button("Multiplayer");
      
      btnSingleplayer.setPrefWidth(300);
      btnMultiplayer.setPrefWidth(300);
      
      btnSingleplayer.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupSingleplayer());
            }
         });
         
      btnMultiplayer.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupMultiplayer());
            }
         });
         
      root.setAlignment(Pos.CENTER);
      root.getChildren().addAll(btnSingleplayer, btnMultiplayer);
      
      // Return root
      return root;
   }
   
   /* Create and return the SinglePlayer Race GUI */
   public Parent setupSingleplayer() {
      // Create new root pane
      VBox root = new VBox();
      root.setPadding(new Insets(10, 10, 10, 10));
      
      // Initialize Race
      initRace();
      
      // Row 0
      HBox fpControls = new HBox();
      Pane paneSpacer = new Pane();
      Button btnBack = new Button("Back");
      Button btnDebug = new Button("Debug");
      //Label lblFPS = race.getFrame();
      //showDebbuger();
      
      btnDebug.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               showDebugger();
            }
         });
      
      btnBack.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupMenu());
               race.stop();
            }
         });
      
      fpControls.setHgrow(paneSpacer, Priority.ALWAYS);
      fpControls.getChildren().addAll(btnBack, paneSpacer, btnDebug);
      root.getChildren().add(fpControls);
   
      // Row 1
      Parent map = race.getMap();
      root.getChildren().add(map);
      
      // Start race
      race.start();
      
      // Return root
      return root;
   }
   
   /* Create and return the MultiPlayer Race GUI */
   public Parent setupMultiplayer() {
      // Create new root pane
      VBox root = new VBox();
      
      Button btnBack = new Button("Back");
      
      btnBack.setPrefWidth(300);
      btnBack.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupMenu());
            }
         });
         
      root.setAlignment(Pos.CENTER);
      root.getChildren().addAll(btnBack);
      
      // Return root
      return root;
   }
   
   /* Create race and add racer to it */
   public void initRace() {
      race = new Race(RACE_MAP, RACE_MASK, sceneWidth, sceneHeight);
      //race.setRacer(new Racer("Dinko", RACER_START_X, RACER_START_Y, RACER_START_DEG));
      race.setRacer(new Racer("Dinko"));
   }
   
   /* Debugger */
   public void showDebugger() {
      VBox root = new VBox();
      Label lblFPS = race.getFrame();
      root.getChildren().add(lblFPS);
      Stage stage = new Stage();
      stage.setTitle("Debug");
      stage.setScene(new Scene(root, 300, 300));
      stage.show();
   }
   
   /* Keyboard Handler */
   public void handleKeys() {
      // Create racer
      Racer racer;
      
      // If race does not exist then return from method
      try {
         racer = race.getRacer();
      } catch (NullPointerException e) {
         return;
      }
      
      /* KEY PRESSED */
      scene.addEventFilter(KeyEvent.KEY_PRESSED, 
         key -> {
         
            switch (key.getCode()) {
               case UP: case W:
                  racer.goFoward(true);
                  break;
               case DOWN: case S:
                  racer.goBackward(true);
                  break;
               case LEFT: case A:
                  racer.goLeft(true);
                  break;
               case RIGHT: case D:
                  racer.goRight(true); 
                  break;
               case SHIFT:
                  racer.goTurbo(true);
                  break;
               case F3:
                  showDebugger();
                  break;
            }
            
         });
             
      /* KEY RELEASED */
      scene.addEventFilter(KeyEvent.KEY_RELEASED, 
         key -> {
         
            switch (key.getCode()) {
               case UP: case W:
                  racer.goFoward(false);
                  break;
               case DOWN: case S:
                  racer.goBackward(false);
                  break;
               case LEFT: case A:
                  racer.goLeft(false);
                  break;
               case RIGHT: case D:
                  racer.goRight(false);
                  break;
               case SHIFT: 
                  racer.goTurbo(false);
                  break;
            }
         });
   }
}