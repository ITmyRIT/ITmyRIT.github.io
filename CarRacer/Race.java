import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
 * Race Class
 * Used for creating new race and updating racers
 */

public class Race extends AnimationTimer {

   /* Image */
   private String roadImage;
   
   /* Size */
   private int roadWidth;
   private int roadHeight;
   
   /* Car */
   private Racer racer;
   
   /* FPS */
   private Label fps;
   
   public Race(String roadImage, int roadWidth, int roadHeight){
      this.roadImage = roadImage;
      this.roadWidth = roadWidth;
      this.roadHeight = roadHeight;
      this.fps = new Label();
   }
   
   public void setRacer(Racer racer){
      this.racer = racer;
   }
   
   public Racer getRacer(){
      return this.racer;
   }
   
   public StackPane getMap(){
      // Create Panes
      StackPane root = new StackPane();
      ImageView map = new ImageView(roadImage);
      
      // Modify Size of Map
      map.setFitWidth(roadWidth);
      map.setFitHeight(roadHeight);
      map.setPreserveRatio(true);
      
      // Append Map to root
      root.getChildren().addAll(map, getRacerPane());      
      
      // Return root
      return root;
   }
   
   public StackPane getRacerPane() {
      StackPane root = new StackPane();
      root.getChildren().add(racer);
      return root;
   }
   
   public void setFrame(long fps) {
      this.fps.setText(String.format("Frame: %d", fps));
   }
   
   public Label getFrame() {
      return this.fps;
   }
   
   @Override public void handle(long timeStamp) {
      racer.update();
      setFrame(timeStamp);
   }
}