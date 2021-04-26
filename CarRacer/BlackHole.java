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
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.util.*;
import java.lang.Math;

/*
 * Final Project - Car Racer 
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * Racer Class
 * Represents individual Racer
 */
 
public class BlackHole extends VBox {
   private final static String holeImage = "assets/hole5.png";
   private final static double ROTATION_DEG = 2;
   private final static int TIMER_TICK_RATE = 4;
   
   private double startX = 0;
   private double startY = 0;
   private double startRotation = 0;
   private double positionX = 0;
   private double positionY = 0;
   private double rotation = 0;
   private int holeHeight = 50;
   private int holeWidth = 50;
   double roadWidth = 0;
   double roadHeight = 0;
      
   // Movement Timers
   private Timer moveTimer = new Timer();
   private TimerTask taskRotate;
   
   // Old movement states
   private boolean rotating = false;
   
   // Images
   private Image holeImg = null;
   private ImageView holeView = null;
   
   private Image roadImg = null;
   private StackPane imgStack = null;

   public BlackHole() {
      imgStack = new StackPane();
      this.holeImg = new Image(holeImage, holeWidth, holeHeight, true, true);
      this.holeView = new ImageView(holeImg);
      this.imgStack.getChildren().add(holeView);
      this.setAlignment(Pos.CENTER);
      this.getChildren().addAll(imgStack);
      this.rotate(true);
      this.doRespawn();
   }
   
   public BlackHole(double startX, double startY, double startDeg) {
      this();
      this.startX = startX;
      this.startY = startY;
      this.startRotation = startDeg;
      this.positionX = startX;
      this.positionY = startY;
      this.rotation = startDeg;
   }
   
   
   public BlackHole(Image roadImg, double w, double h) {
      this();
      this.roadImg = roadImg;
      this.roadHeight = h;
      this.roadWidth = w;
      this.respawn();
   }
   
   public void respawn() {
      this.setPositionX(0);
      this.setPositionY(0);
      while (!checkIfOnMap()) {
         this.setPositionX(Math.random()*roadWidth);
         this.setPositionY(Math.random()*roadHeight);
      }
   }
   
   public boolean checkIfOnMap() {
      try { 
         PixelReader pixelReader = roadImg.getPixelReader();
         int centerX = (int)this.getPositionX()+(int)(this.getHoleWidth()/2);
         int centerY = (int)this.getPositionY()+(int)(this.getHoleHeight()/2);
         Color color = pixelReader.getColor(centerX, centerY);
      
         if (color.equals(Color.rgb(0,255,0))) {
            System.out.println("ON MAP");
            return true;
         }
         
         return false;
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      return false;
   }

   
   public void doRespawn() {
      Timer myTimer = new Timer();
      myTimer.scheduleAtFixedRate(
         new TimerTask() {
            @Override public void run() {
               respawn();
            }}, 0, 1000);
   }
   
   public void hide() {
      this.holeView.setOpacity(0);
   }

   // Rotation
   public void rotate(boolean go){
      if (go && !rotating) {
         taskRotate = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     rotation+=ROTATION_DEG;
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskRotate, 0, TIMER_TICK_RATE);
         rotating = true;
      } else if (!go && rotating){
         taskRotate.cancel();
         rotating=false;
      }
   }


   public boolean isRotating() {
      return this.rotating;
   }

   public double getPositionX () {
      return this.positionX;
   }
   
   public double getPositionY () {
      return this.positionY;
   }
   
   public double getRotation(){
      return this.rotation;
   }
   
   public int getHoleWidth() {
      return this.holeWidth;
   }
   
   public int getHoleHeight() {
      return this.holeHeight;
   }
   
   public void setPositionX(double x) {
      this.positionX = x;
   }
   
   public void setPositionY(double y) {
      this.positionY = y;
   }
   
   public void setRotation(double deg) {
      this.rotation = deg;
   }
   
   
   public void update() {
      if (rotation>360) rotation -= 360;
      if (rotation<-360) rotation += 360;
      this.setTranslateX(positionX);
      this.setTranslateY(positionY);
      this.imgStack.setRotate(rotation);
   }
}