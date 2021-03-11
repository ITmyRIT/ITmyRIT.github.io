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
import javafx.scene.image.*;

/*
 * Final Project - Car Racer 
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * Race Class
 * Used for creating new race and updating racers
 */

public class Race extends AnimationTimer {

   /* Image */
   private String roadImage;
   private String roadMask;
   
   /* Size */
   private int roadWidth;
   private int roadHeight;
   
   /* Car */
   private Racer racer;
   
   /* FPS */
   private Label fps;
   
   public Race(String roadImage, String roadMask, int roadWidth, int roadHeight){
      this.roadImage = roadImage;
      this.roadMask = roadMask;
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
      Image mapImg = new Image(roadImage, roadWidth, roadHeight, true, true);
      Image maskImg = new Image(roadMask, roadWidth, roadHeight, true, true);
      
      ImageView map = new ImageView(mapImg);
      ImageView mask = new ImageView(maskImg);

      // Append Map to root
      root.getChildren().addAll(mask, map, getRacerPane());      
      
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
   
   public void checkCollison(Racer racer) {
      boolean onMap = racer.isRacerAt((int)roadWidth/2, (int)roadHeight/2, roadWidth, roadHeight);
      System.out.println(onMap);
   }
   
   public void setShowSpeed(double speed) {
      this.fps.setText(String.format("MaxSpeed: %.4f", speed));
   }
   
   public void setDebug(String deb) {
      this.fps.setText(deb);
   }
   
   @Override public void handle(long timeStamp) {
      racer.update();
      checkCollison(racer);
      
      //setFrame(timeStamp);
      //setShowSpeed(racer.getMaxSpeed());
      setDebug(racer.doDebug());
   }
}