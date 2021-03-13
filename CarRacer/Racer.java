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
 
public class Racer extends StackPane {
   private final static String racerImage = "assets/car.png";
   private final static String racerMask = "assets/car-mask.png";
   private final static double DRAG_FORCE = 0.005;
   private final static double ENGINE_FORCE = 0.05;
   private final static double ROTATION_DEG = 0.6;
   private final static int TIMER_TICK_RATE = 4;
   
   private double positionX = 0;
   private double positionY = 0;
   private double speed = 0.0;
   private double maxSpeed = 2.0;
   private double oldMaxSpeed = maxSpeed;
   private double maxTurboSpeed = 4.0;
   private double rotation = 0;
   private int racerHeight = 30;
   private int racerWidth = 40;
   private String name;
   
   // Movement Timers
   private Timer moveTimer = new Timer();
   private TimerTask taskForward;
   private TimerTask taskBackward;
   private TimerTask taskLeft;
   private TimerTask taskRight;
   private TimerTask taskTurbo;
   private TimerTask taskRotate;
   
   // Old movement states
   private boolean goingForward = false;
   private boolean goingBackward = false;
   private boolean goingLeft = false;
   private boolean goingRight = false;
   private boolean goingTurbo = false;
   private boolean rotating = false;
   
   // Images
   private Image racerImg = null;
   private Image maskImg = null;
   private ImageView racerView = null;
   private ImageView maskView = null;
   
   public Racer(String name) {
      this.racerImg = new Image(racerImage, racerWidth, racerHeight, true, true);
      this.maskImg = new Image(racerMask, racerWidth, racerHeight, true, true);
      
      this.racerView = new ImageView(racerImg);
      this.maskView = new ImageView(maskImg);
      
      this.getChildren().addAll(racerView, maskView);
      this.name = name;
      this.simulateDrag();
      this.toggleMask();
   }
   
   public Racer(String name, double startX, double startY, double startDeg) {
      this.racerImg = new Image(racerImage, racerWidth, racerHeight, true, true);
      this.maskImg = new Image(racerMask, racerWidth, racerHeight, true, true);
      
      this.racerView = new ImageView(racerImg);
      this.maskView = new ImageView(maskImg);
      
      this.getChildren().addAll(racerView, maskView);
      this.name = name;
      this.simulateDrag();
      this.name = name;
      this.positionX = startX;
      this.positionY = startY;
      this.rotation = startDeg;
      this.simulateDrag();
      this.toggleMask();
   }
   
   public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
   }
   
   /* Movement */
   
   // Forward
   public void goFoward(boolean go) {
      if (go && !goingForward) {
         taskForward = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     speed+=ENGINE_FORCE;
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskForward, 0, TIMER_TICK_RATE);
         goingForward = true;
      } else if (!go && goingForward){
         taskForward.cancel();
         goingForward = false;
      };
   }
   
   // Backward
   public void goBackward(boolean go) {
      if (go && !goingBackward) {
         taskBackward = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     speed-=ENGINE_FORCE;
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskBackward, 0, TIMER_TICK_RATE);
         goingBackward = true;
      } else if (!go && goingBackward){
         taskBackward.cancel();
         goingBackward = false;
      }
   }
   
   // Left
   public void goLeft(boolean go) {
      if (go && !goingLeft) {
         taskLeft = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     rotation-=ROTATION_DEG*(speed==0?0:speed/maxSpeed);
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskLeft, 0, TIMER_TICK_RATE);
         goingLeft = true;
      } else if (!go && goingLeft){
         taskLeft.cancel();
         goingLeft = false;
      }
   }
   
   // Right
   public void goRight(boolean go) {
      if (go && !goingRight) {
         taskRight = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     rotation+=ROTATION_DEG*(speed==0?0:speed/maxSpeed);
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskRight, 0, TIMER_TICK_RATE);
         goingRight = true;
      } else if (!go && goingRight){
         taskRight.cancel();
         goingRight = false;
      }
   }
   
   public void goTurbo(boolean go) {
      if (taskTurbo!=null) {
         taskTurbo.cancel();
      }
      if (go && !goingTurbo) {
         taskTurbo = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     maxSpeed+=ENGINE_FORCE;
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskTurbo, 0, TIMER_TICK_RATE);
         goingTurbo = true;
      } else if (!go && goingTurbo){
         taskTurbo.cancel();
         taskTurbo = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     if(maxSpeed>oldMaxSpeed) maxSpeed-=ENGINE_FORCE;
                     else {
                        maxSpeed = oldMaxSpeed;
                        taskTurbo.cancel();
                     }
                  }
               }
            };
         moveTimer.scheduleAtFixedRate(taskTurbo, 0, TIMER_TICK_RATE);
         goingTurbo=false;
      }
   }
   
   // Drag
   public void simulateDrag(){
      moveTimer.scheduleAtFixedRate(
         new TimerTask() {
            @Override public void run() {
               synchronized(moveTimer) {
                  if (speed>0) {
                     speed-=DRAG_FORCE;
                  } else if (speed<0) {
                     speed+=DRAG_FORCE;
                  }
                  if (Math.abs(speed)<0.01) {
                     speed = 0;
                  }
               }
            }
         }, 0, TIMER_TICK_RATE);
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

   public String doDebug() {
      return String.format(
         "Position X: %.2f\n"+
         "Position Y: %.2f\n"+
         "Rotation: %.2f\n"+
         "Speed: %.2f\n"+
         "MaxSpeed: %.2f\n"+
         "OldMax: %.2f\n"+
         "MaxTurbo: %.2f\n",
         this.positionX,
         this.positionY,
         this.rotation,
         this.speed,
         this.maxSpeed,
         this.oldMaxSpeed,
         this.maxTurboSpeed
         );
   }
   
   public double getMaxSpeed() {
      return this.maxSpeed;
   }
   
   public double getPositionX () {
      return this.positionX;
   }
   
   public double getPositionY () {
      return this.positionY;
   }
   
   public int getRacerWidth() {
      return this.racerWidth;
   }
   
   public int getRacerHeight() {
      return this.racerHeight;
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
      if (maxSpeed>maxTurboSpeed) maxSpeed = maxTurboSpeed;
      if (-maxSpeed>maxTurboSpeed) maxSpeed = -maxTurboSpeed;
      if (speed>maxSpeed) speed = maxSpeed;
      if (-speed>maxSpeed) speed = -maxSpeed;
      if (rotation>360) rotation -= 360;
      if (rotation<-360) rotation += 360;
      this.positionX+=Math.cos(Math.toRadians(rotation))*speed;
      this.positionY+=Math.sin(Math.toRadians(rotation))*speed;
      this.setTranslateX(positionX);
      this.setTranslateY(positionY);
      this.setRotate(rotation);
   }
}