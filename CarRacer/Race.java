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
   private ArrayList<Racer> racers;
   
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
   
   /* Networking */
   private static final int SERVER_PORT = 12345;
   
   Racer racer2;
   
   // SinglePlayer
   public Race(String roadImage, String roadMask, int roadWidth, int roadHeight, boolean multiplayer){
      this.roadImage = roadImage;
      this.roadMask = roadMask;
      this.roadWidth = roadWidth;
      this.roadHeight = roadHeight;
      this.fps = new Label();
      this.racerPane = new StackPane();
      this.racersPane = new StackPane();
      this.racers = new ArrayList<Racer>();
      if (multiplayer) this.client = new CarRacerClient(/*"192.168.0.164"*/null, SERVER_PORT);
      this.racer2 = new Racer("Dinko", 0, 0, 0);
      racersPane.getChildren().add(this.racer2);
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
      
      Position position = new Position();
      position.setPositionX(racer.getPositionX());
      position.setPositionY(racer.getPositionY());
      position.setRotation(racer.getRotation());
      
      
      this.client.sendObject(position);
      
      position = (Position)this.client.receiveObject();
      racer2.setPositionX(position.getPositionX());
      racer2.setPositionY(position.getPositionY());
      racer2.setRotation(position.getRotation());
      
      /*
      this.racers = (ArrayList<Racer>) this.client.receiveObject();
      this.racersPane = new StackPane();
      for(Racer r:racers) racersPane.getChildren().add(r);
      */
   }
   
   @Override public void handle(long timeStamp) {
      updateRacers();
      
      racer.update();
      racer2.update();
      
      
      
      setDebug(racer.doDebug());
   }
}