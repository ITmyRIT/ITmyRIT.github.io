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
   
   /* Racer */
   private Racer racer;
   private Vector<Racer> racers;
   
   /* FPS */
   private Label fps;
   
   /* Images */
   private Image mapImg = null;
   private Image maskImg = null;
   private ImageView mapView = null;
   private ImageView maskView = null;
   
   /* Client */
   CarRacerClient client = null;
   
   /* Pane with all racers */
   StackPane racerPane = null;
   StackPane racersPane = null;
   
   /* My Position */
   Position myPosition = new Position();
   
   /* Networking */
   private static final int SERVER_PORT = 12345;
         
   public Race(String roadImage, String roadMask, int roadWidth, int roadHeight){
      this.roadImage = roadImage;
      this.roadMask = roadMask;
      this.roadWidth = roadWidth;
      this.roadHeight = roadHeight;
      this.fps = new Label();
      this.racerPane = new StackPane();
      this.racersPane = new StackPane();
   
   
      client = new CarRacerClient("", 12345);
      //client.start();
   
   }
   
   public void setRacer(Racer racer){
      this.racer = racer;
      this.racerPane.getChildren().add(racer);
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
      root.getChildren().addAll(mapView, maskView, racerPane, racersPane);      
      toggleMask();
      
      // Return root
      return root;
   }
   
   public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
   }

   public void setDebug(String deb) {
      this.fps.setText(deb);
   }
   
   public Label getFrame() {
      return this.fps;
   }
   
   public void updateRacers() {
                         
   }
   
   @Override public void handle(long timeStamp) {
   
      racer.update();
   
         new Thread() {
            public void run() {
               Platform.runLater(
                     new Runnable() {
                        public void run() {
                           //updateRacers();
                        }
                     }
                     );
            }
         }.start();
      
      setDebug(racer.doDebug());
   }
}