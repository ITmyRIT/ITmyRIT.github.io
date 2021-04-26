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

/**
 * Final Project - Car Racer
 * @author Mislav Breka 
 * @author Karlo Longin
 * @author Marko Obsivac
 * Racer Class
 * Represents individual Racer
 */
public class Racer extends  VBox {
  /** Importing the image of the car and its mask */
   private final static String racerImage = "assets/car.png";
   private final static String racerMask = "assets/car-mask-blue.png";
   
   /** The physics: drag force, engine force, rotation degree */
   private final static double DRAG_FORCE = 0.005;
   private final static double ENGINE_FORCE = 0.03;
   private final static double ROTATION_DEG = 0.7;
   private final static int TIMER_TICK_RATE = 4;
   
   /** The racer's attributes */
   private double startX = 0;
   private double startY = 0;
   private double startRotation = 0;
   private double positionX = 0;
   private double positionY = 0;
   private double speed = 0.0;
   private double maxSpeed = 2.4;
   private double oldMaxSpeed = maxSpeed;
   private double maxTurboSpeed = 5.0;
   private double rotation = 0;
   private int racerHeight = 15;
   private int racerWidth = 22;
   
   /**  Movement Timers */
   private Timer moveTimer = new Timer();
   private TimerTask taskForward;
   private TimerTask taskBackward;
   private TimerTask taskLeft;
   private TimerTask taskRight;
   private TimerTask taskTurbo;
   private TimerTask taskRotate;
   
   /** Old movement states */
   private boolean goingForward = false;
   private boolean goingBackward = false;
   private boolean goingLeft = false;
   private boolean goingRight = false;
   private boolean goingTurbo = false;
   private boolean rotating = false;
   
   /** Images */
   private Image racerImg = null;
   private Image maskImg = null;
   private ImageView racerView = null;
   private ImageView maskView = null;
   private StackPane imgStack = null;
   

   /** Nickname */
   private Label lblNickname = null;
   private String nickname = null;
   
   /** Keyboard control */
   private boolean keyboard = true;
   
   /** Laps and Progress */
   private int laps = 0;
   private double progress = 0.0;
   
   /** Creates a new racer, displays the image and mask, displays the label with the users nickname, and takes the user's input for the nickname. 
     * @param nickname An identifier for the user.
     */
   public Racer(String nickname) {
      this.nickname = nickname;
      
      imgStack = new StackPane();
      this.racerImg = new Image(racerImage, racerWidth, racerHeight, true, true);
      this.maskImg = new Image(racerMask, racerWidth, racerHeight, true, true);
      
      this.racerView = new ImageView(racerImg);
      this.maskView = new ImageView(maskImg);
      
      imgStack.getChildren().addAll(racerView, maskView);
   
      lblNickname = new Label(nickname);
      lblNickname.setTextFill(Color.rgb(255, 255, 255));
      
      lblNickname.setTranslateY(-50);
      
      lblNickname.setBackground(new Background(
         new BackgroundFill(
            Color.rgb(66,66,66,0.6),
            new CornerRadii(5),
            null)));
      
      this.setAlignment(Pos.CENTER);
      this.getChildren().addAll(imgStack, lblNickname);
      this.simulateDrag();
      this.toggleMask();
   }
   
    /** Calls the super constructor and sets the starting position. 
     * @param nickname An identifier for the user.
     */
   public Racer(String nickname, double startX, double startY, double startDeg) {
      this(nickname);
      this.startX = startX;
      this.startY = startY;
      this.startRotation = startDeg;
      this.positionX = startX;
      this.positionY = startY;
      this.rotation = startDeg;
   }
   
   /** Toggles the visibility of the mask (debugging). */
   public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
   }
   
   /** Hides the visibility of the lagging car, used because of the difficulties with connection. */
   public void hide() {
      this.racerView.setOpacity(0);
      this.maskView.setOpacity(0);
      this.lblNickname.setVisible(false);
   }
   
   /** Movement */
   
   //Forward
   
   /** Adds speed to the racer in the forward direction.
    * @param go A boolean which denotes whether the racer is in forward motion.   
    */
   public void goFoward(boolean go) {
      if (!keyboard) 
         return;
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
   
   /** Adds speed to the racer in the backward direction.
    * @param go A boolean which denotes whether the racer is in backward motion.   
    */
   public void goBackward(boolean go) {
      if (!keyboard) 
         return;
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
   
   /** Rotates the car in the left direction.
    * @param go A boolean which denotes whether the racer is going left.   
    */
   public void goLeft(boolean go) {
      if (!keyboard) 
         return;
      if (go && !goingLeft) {
         taskLeft = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     rotation-=ROTATION_DEG*(Math.abs(speed)<2?(speed/oldMaxSpeed):(oldMaxSpeed/speed));
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
   
   /** Rotates the car in the right direction.
    * @param go A boolean which denotes whether the racer is going right.   
    */
   public void goRight(boolean go) {
      if (!keyboard) 
         return;
      if (go && !goingRight) {
         taskRight = 
            new TimerTask() {
               @Override public void run() {
                  synchronized(moveTimer) {
                     rotation+=ROTATION_DEG*(Math.abs(speed)<2?(speed/oldMaxSpeed):(oldMaxSpeed/speed));
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
   
   //Turbo
   
   /** Increases the max speed so the car can faster.
    * @param go A boolean which denotes whether the racer is using turbo.   
    */
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
   
   /** Slows down the car when not adding speed. */
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
   
   /** Rotates the racer in place (debugging)
     * @param go A boolean which denotes whether the racer is rotating.
     */
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
   
   /** Disables the user's input */
   public void loseControl() {
      this.setRacerOpacity(0.6);
      this.goLeft(false);
      this.goRight(false);
      this.goFoward(false);
      this.goBackward(false);
      if(speed>0) this.goFoward(true);
      if(speed<0) this.goBackward(true);
      this.keyboard = false;
   }
   
   /** Enables the user's input */
   public void findControl() {
      this.setRacerOpacity(1);
      this.keyboard = true;
      this.goLeft(false);
      this.goRight(false);
      this.goFoward(false);
      this.goBackward(false);
   }
   
   /** Relocates the user to the starting position, decrements their lap by 1, and does this when it hits the edge of the screen. */
   public void respawn() {
      findControl();
      this.speed = 0;
      this.positionX = startX;
      this.positionY = startY;
      this.rotation = startRotation;
      this.laps--;
   }

   /** Returns a boolean which denotes if the racer is rotating. */
   public boolean isRotating() {
      return this.rotating;
   }
   
   /** Creates a seperate GUI which shows the users current position, rotation and speed */
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
   
   public double getRotation(){
      return this.rotation;
   }
   
   public int getRacerWidth() {
      return this.racerWidth;
   }
   
   public int getRacerHeight() {
      return this.racerHeight;
   }
   
   public int getLaps() {
      return this.laps;
   }
   
   public double getProgress() {
      return this.progress;
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
   
   public void setLaps(int laps) {
      this.laps = laps;
   }
   
   public void setProgress(double progress) {
      this.progress = progress;
   }
   
   
   public void setRacerOpacity(double op) {
      this.racerView.setOpacity(op);
   }
   
   public double getRacerOpacity() {
      return this.racerView.getOpacity();
   }
   
   public void setKeyboard(boolean keyboard) {
      this.keyboard = keyboard;
   }
   public boolean getKeyboard() {
      return this.keyboard;
   }
   
   public void setSpeed(double speed) {
      this.speed = speed;
   }

   
   public void setPosition(Position pos) {
      this.setPositionX(pos.getPositionX());
      this.setPositionY(pos.getPositionY());
      this.setRotation(pos.getRotation());
      this.setNickname(pos.getNickname());
      this.setRacerOpacity(pos.getOpacity());
      this.setProgress(pos.getProgress());
      this.setLaps(pos.getLaps());
   }
   
   public Position getPosition() {
      return new Position(
         this.getPositionX(),
         this.getPositionY(),
         this.getRotation(),
         this.getNickname(),
         this.getRacerOpacity(),
         this.getLaps(),
         this.getProgress()
         );
   }
   

   public String getNickname(){
      return this.nickname;
   }
   
   public void setNickname(String nickname) {
      this.lblNickname.setText(nickname);
      this.nickname = nickname;
   }

   /** Called by the Animation Timer, updates the position all racers. */
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
      this.imgStack.setRotate(rotation);
      
   }
}