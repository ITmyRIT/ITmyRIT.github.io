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
   private int sceneWidth = 800;
   private int sceneHeight = 400;

   /* Race attributes */
   private Race race;

   /* Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /* Method called by launch(args) */
   @Override public void start(Stage stage) {
      this.stage = stage;
      
      stage.setResizable(false);
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
      scene = new Scene(root, sceneWidth, sceneHeight);
      handleKeys();
      stage.setScene(scene);
      stage.show();
   }
   
   /* Create and return the Menu GUI */
   public Parent setupMenu() {
      // Create new root pane
      VBox root = new VBox();
      
      Button btnStart = new Button("Start");
      
      btnStart.setPrefWidth(300);
      btnStart.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupRace());
            }
         });
         
      root.setAlignment(Pos.CENTER);
      root.getChildren().addAll(btnStart);
      
      // Return root
      return root;
   }
   
   /* Create and return the Race GUI */
   public Parent setupRace() {
      // Create new root pane
      VBox root = new VBox();
      root.setPadding(new Insets(10, 10, 10, 10));
      
      // Initialize Race
      initRace("assets/road.png");
      
      // Row 0
      HBox fpControls = new HBox();
      Pane paneSpacer = new Pane();
      Button btnStop = new Button("Stop");
      Label lblFPS = race.getFrame();
      
      btnStop.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               updateScene(setupMenu());
               race.stop();
            }
         });
      
      fpControls.setHgrow(paneSpacer, Priority.ALWAYS);
      fpControls.getChildren().addAll(btnStop, paneSpacer, lblFPS);
      root.getChildren().add(fpControls);
   
      // Row 1
      Parent map = race.getMap();
      root.getChildren().add(map);
      
      // Start race
      race.start();
      
      // Return root
      return root;
   }
   
   /* Start race */
   public void initRace(String raceMap) {
      race = new Race(raceMap, sceneWidth, sceneHeight);
      race.setRacer(new Racer("Dinko"));
   }
   
   /* Keyboard Handler */
   public void handleKeys() {
      // Create racer
      Racer racer;
      
      // If race does not exist then return
      try {
         racer = race.getRacer();
      } catch (NullPointerException e) {
         return;
      }
      
      /* KEY PRESSED */
      scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
         
            switch (key.getCode()) {
               case UP:
                  racer.goFoward(true);
                  break;
               case DOWN:
                  racer.goBackward(true);
                  break;
               case LEFT: 
                  racer.goLeft(true);
                  break;
               case RIGHT:
                  racer.goRight(true); 
                  break;
               case SHIFT:
                     
                  break;
            }
            
         });
             
      /* KEY RELEASED */
      scene.addEventFilter(KeyEvent.KEY_RELEASED, key -> {
      
            switch (key.getCode()) {
               case UP:
                  racer.goFoward(false);
                  break;
               case DOWN: 
                  racer.goBackward(false);
                  break;
               case LEFT: 
                  racer.goLeft(false);
                  break;
               case RIGHT:
                  racer.goRight(false);
                  break;
               case SHIFT: 
                     
                  break;
            }
         });
   }
}