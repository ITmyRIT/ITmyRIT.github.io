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
   
   // Images
   private Image mapImg = null;
   private Image maskImg = null;
   private ImageView mapView = null;
   private ImageView maskView = null;
   
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
      mapImg = new Image(roadImage, roadWidth, roadHeight, true, true);
      maskImg = new Image(roadMask, roadWidth, roadHeight, true, true);
      
      mapView = new ImageView(mapImg);
      maskView = new ImageView(maskImg);
      
      // Append Map to root
      root.getChildren().addAll(mapView, maskView, getRacerPane());      
      toggleMask();
      
      // Return root
      return root;
   }
   
    public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
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
      int centerWidth = (int)roadWidth/2;
      int centerHeight = (int)roadHeight/2;
      
      /*
      positionX = 0
      positionY = 0
      private int racerHeight = 30;
      private int racerWidth = 40;
      private int sceneWidth = 1000;
      private int sceneHeight = 600;
      x = 500
      y = 300
      */
      
      // for (int x=0;x<(int)roadWidth;x++) {
//          for (int y=0;y<(int)roadHeight;y++) {
//             boolean onMap = racer.isRacerAt(x, y, roadWidth, roadHeight);
//             System.out.println(onMap);
//          }
//       }

      //PixelReader pReader = maskImg.getPixelReader();
      //Color color = pReader.getColor(x, y);
      //System.out.println(color);
   }
   
   public String getLocation(Racer racer){
      int centerWidth = (int)roadWidth/2;
      int centerHeight = (int)roadHeight/2;
      
      String locationString = "";
      
      // Left
      if (racer.getPositionX()+(roadWidth/2)+(racer.getRacerWidth()/2)<centerWidth) {
         locationString += "LEFT ";
      }
      // Right
      if (racer.getPositionX()+(roadWidth/2)-(racer.getRacerWidth()/2)>centerWidth) {
         locationString += "RIGHT ";
      }
      // Top
      if (racer.getPositionY()+(roadHeight/2)+(racer.getRacerHeight()/2)<centerHeight) {
         locationString += "TOP ";
      }
      // Bottom
      if (racer.getPositionY()+(roadHeight/2)-(racer.getRacerHeight()/2)>centerHeight) {
         locationString += "BOTTOM ";
      }
      
      if (locationString.equals("")) {
         return "CENTER";
      }

      return locationString;
   }
   
   double realRacerPosX = 0;
   double realRacerPosY = 0;
   public boolean isRacerAtRoad(Racer racer){
      boolean out = true;
      
      realRacerPosX = (roadWidth/2) + racer.getPositionX();
      realRacerPosY = (roadHeight/2) + racer.getPositionY();
      
      return out;
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
      setDebug(racer.doDebug()+String.format(
         "Location: %s\n"+
         "RealPosX: %f\n"+
         "RealPosY: %f\n",
         this.getLocation(racer),
         this.realRacerPosX,
         this.realRacerPosY
      ));
   }
}