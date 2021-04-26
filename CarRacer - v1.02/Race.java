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
import javafx.scene.paint.Color;

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
   private String roadPosition;
   
   /* Size */
   private int roadWidth;
   private int roadHeight;
   
   /* Player */
   private String nickname = "";
   private Racer racer;
   
   /* Label Debug */
   private Label lblDebug;
   
   /* Images */
   private Image mapImg = null;
   private Image maskImg = null;
   private Image positionImg = null;
   private ImageView mapView = null;
   private ImageView maskView = null;
   
   /* Client */
   CarRacerClient client = null;
   
   /* Pane with all racers */
   StackPane racerPane = null;
   StackPane racersPane = null;
   
   Hashtable<String, Racer> racers = new Hashtable<String, Racer>();
   
   /* My Position */
   double oldPosition = 0.0;
   
   /* Networking */
   private static final int SERVER_PORT = 12345;
         
   /* Debuging */
   private String collisionDebug = "";
   private String positionDebug = "";
   
   /* Finish Pane */
   private Parent finishPane = null;
   
   /* Leaderboard */
   private Label leaderboardLabel = null;
   
   /* Chat */
   private Label chatLabel = null;
   
   public Race(String roadImage, String roadMask, String roadPosition, int roadWidth, int roadHeight){
      this.roadImage = roadImage;
      this.roadMask = roadMask;
      this.roadPosition = roadPosition;
      this.roadWidth = roadWidth;
      this.roadHeight = roadHeight;
      this.lblDebug = new Label();
      this.racerPane = new StackPane();
      this.racersPane = new StackPane();
   }
   
   public boolean connectToServer() {
      try {
         this.client = new CarRacerClient("", 12345, racer);
         this.client.start();
         return true;
      } catch (Exception e) {
         return false;
      }
   }
   
   public void setRacer(Racer racer){
      this.racer = racer;
      this.racerPane.getChildren().add(racer);
   }
   
   public Racer getRacer(){
      return this.racer;
   }
   
   public CarRacerClient getClient () {
      return this.client;
   }
   
   public StackPane getMap(){
      // Create Panes
      StackPane root = new StackPane();
      mapImg = new Image(roadImage, roadWidth, roadHeight, true, true);
      maskImg = new Image(roadMask, roadWidth, roadHeight, true, true);
      positionImg = new Image(roadPosition, roadWidth, roadHeight, true, true);
      
      mapView = new ImageView(mapImg);
      maskView = new ImageView(maskImg);
      
      // Append Map to pane
      Pane pane = new Pane();
      
      pane.setPrefHeight(roadHeight);
      pane.setPrefWidth(roadWidth);
      
      pane.getChildren().addAll(mapView, maskView, racerPane, racersPane);      
     
      root.getChildren().add(pane);
      toggleMask();
      
      // Return root
      return root;
   }
   
   public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
   }

   public void setDebug(String deb) {
      this.lblDebug.setText(deb);
   }
   
   public Label getFrame() {
      return this.lblDebug;
   }
   
   public void updateRacers() {
      Hashtable<String, Position> positions = client.getPositions();
      for (String key:positions.keySet()) {
         if (racers.containsKey(key)) {
            racers.get(key).setPosition(positions.get(key));
            racers.get(key).update();  
         } else {
            Racer racer = new Racer(key);
            racer.setPosition(positions.get(key));
            racers.put(key, racer);
            racersPane.getChildren().add(racer);         
         }
         
         if (racer.getNickname().equals(key)) {
            racers.get(key).hide();
         }
      }
      
      // Check for disconnected racers
      for (String key:racers.keySet()) {
         if (!positions.containsKey(key)) {
            racersPane.getChildren().remove(racers.get(key)); 
            racers.remove(key);
         }
      }
   }
   
   public void updateLeaderBoard() {
      Vector<Position> leaderboard = client.getLeaderBoard();
      
      String out = "";
      for (Position p:leaderboard) {
         out+=String.format("NAME: %-20s LAP: %3d PROG: %6.2f%%\n",
            p.getNickname(), p.getLaps(), p.getProgress()*100
            );
      }
      out += "\n\n";
      final String OUT = out;
      Platform.runLater(
            new Runnable() {
               public void run() {
                  leaderboardLabel.setText(OUT); 
               }
            }
         );
   }
   
   
   public void checkCollision() {
      try { 
         PixelReader pixelReader = maskImg.getPixelReader();
         int centerX = (int)racer.getPositionX()+(int)(racer.getRacerWidth()/2);
         int centerY = (int)racer.getPositionY()+(int)(racer.getRacerHeight()/2);
         Color color = pixelReader.getColor(centerX, centerY);
         collisionDebug = String.format(
            "\nCOLORS\nRED: %d\nGREEN: %d\nBLUE: %d\n",
            (int)(color.getRed()*256), (int)(color.getGreen()*256), (int)(color.getBlue()*256)
            );
         if (color.equals(Color.rgb(255,0,0))) {
            System.out.println("OUUUT");
            racer.loseControl();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      // Restart if outside of map
      if (racer.getPositionX()<0 || racer.getPositionY()<0) {
         racer.respawn();
      }
      if (racer.getPositionX()>this.roadWidth || racer.getPositionY()>this.roadHeight) {
         racer.respawn();
      }
   }
   
   public void checkPosition() {
      try { 
         PixelReader pixelReader = positionImg.getPixelReader();
         int centerX = (int)racer.getPositionX()+(int)(racer.getRacerWidth()/2);
         int centerY = (int)racer.getPositionY()+(int)(racer.getRacerHeight()/2);
         Color color = pixelReader.getColor(centerX, centerY);
         positionDebug = String.format(
            "\nPOSITIONS\nRED: %d\nGREEN: %d\nBLUE: %d\n",
            (int)(color.getRed()*256), (int)(color.getGreen()*256), (int)(color.getBlue()*256)
            );
         if (color.getGreen()>0.1) {
            //System.out.println("FINNISH LINE");
         } else {
            double maxPos = 1.0 - 0.3490196168422699;
            double myPos = color.getRed() - 0.3490196168422699;
            double newPosition = myPos/maxPos;
            //System.out.println("POSITION: new:"+newPosition+" old:"+oldPosition);
            updateLaps(newPosition);
            oldPosition = newPosition;
         }
      } catch (Exception e) {
         
      }
   }
   
   
   public void updateLaps(double newPosition) {
   
      racer.setProgress(newPosition);
   
      // Over finnished, forward
      if (isCloseTo(oldPosition, 1.0) && isCloseTo(newPosition, 0.0)) {
         racer.setLaps(racer.getLaps()+1); 
      }
      
      // Over finnished, backward 
      if (isCloseTo(oldPosition, 0.0) && isCloseTo(newPosition, 1.0)) {
         racer.setLaps(racer.getLaps()-1);
      }
   }
   
   public boolean isCloseTo(double x, double y) {
      return Math.abs(x-y)<0.2;
   }
   
   public void setFinishPane(Parent finishPane) {
      this.finishPane = finishPane;
      this.finishPane.setVisible(false);
   }
   
   public void setLeaderBoardLabel(Label leaderboardLabel) {
      this.leaderboardLabel = leaderboardLabel;
   }
   
   public void setChatHistory(Label chatLabel) {
      this.chatLabel = chatLabel;
   }
   
   public void updateChatHistory() {
      Vector<String> chats = client.getChatHistory();
      String out = "";
      for (int i=1;i<(chats.size()<5?chats.size():5);i++) {
         out += chats.get(chats.size()-i) + "\n";
      }
      this.chatLabel.setText(out);
   }
 
   @Override public void handle(long timeStamp) {
      racer.update();
      //checkCollision();
      checkPosition();
      
         new Thread() {
            public void run() {Platform.runLater(
                     new Runnable() {
                        public void run() {
                           updateRacers();
                        }
                     }
                  );
            }
         }.start();
         
      // Show pane if race finished
      if (client.getGameOver()) {
         finishPane.setVisible(true);
         racer.setSpeed(0);
         racer.loseControl();
      }
      
      updateLeaderBoard();
      updateChatHistory();
   
      setDebug(racer.doDebug()+collisionDebug+positionDebug+
         "\nLAPS: "+ racer.getLaps()
         );
   }
}