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

/**
 * Final Project - Car Racer 
 * @author Mislav Breka 
 * @author Karlo Longin
 * @author Marko Obsivac
 * Race Class
 * Used for creating new race and updating racers
 */

public class Race extends AnimationTimer {

   /** Image */
   private String roadImage;
   private String roadMask;
   private String roadPosition;
   
   /** Size */
   private int roadWidth;
   private int roadHeight;
   
   /** Player */
   private String nickname = "";
   private Racer racer;
   
   /** Label Debug */
   private Label lblDebug;
   
   /** Images */
   private Image mapImg = null;
   private Image maskImg = null;
   private Image positionImg = null;
   private ImageView mapView = null;
   private ImageView maskView = null;
   
   /** Client */
   CarRacerClient client = null;
   
   /** Pane with all racers */
   StackPane racerPane = null;
   StackPane racersPane = null;
   
   Hashtable<String, Racer> racers = new Hashtable<String, Racer>();
   
   /** My Position */
   double oldPosition = 0.0;
   
   /** Networking */
   private static final int SERVER_PORT = 12345;
         
   /** Debuging */
   private String collisionDebug = "";
   private String positionDebug = "";
   
   /** Finish Pane */
   private Parent finishPane = null;
   
   /** Leaderboard */
   private Label leaderboardLabel = null;
   private Label finishMessage = null;
   private Label finishBoard = null;
   
   /** Chat */
   private Label chatLabel = null;
   
   /** Map */
   private StackPane map = null;
   
   /** BlackHole */
   private BlackHole blackHole = null;
   
   /** XML Settings */
   private static final String XML_PATH = "ClientConfig.xml";
   
   /** Stores the race position and angle. 
     * @param roadImage The image of the road.
     * @param roadMask The mask of the road, used for collision.
     * @param roadPosition The mask of the road used for measuring the progress of a racer.
     * @param roadWidth The width of the image.
     * @param roadHeight The height of the image.
     */
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
   
   /** Indicates whether it's connected or not. 
    * @return A boolean value.
    */
   public boolean connectToServer() {
      LoadXML xml = new LoadXML(XML_PATH);
      try {
         this.client = new CarRacerClient(xml.getServerIP(), xml.getServerPort(), racer);
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
   
   /** Used for adding the map to the main GUI
    * @return A StackPane (map).
    */
   public StackPane getMap(){
      /** Create Panes */
      map = new StackPane();
      mapImg = new Image(roadImage, roadWidth, roadHeight, true, true);
      maskImg = new Image(roadMask, roadWidth, roadHeight, true, true);
      positionImg = new Image(roadPosition, roadWidth, roadHeight, true, true);
      
      mapView = new ImageView(mapImg);
      maskView = new ImageView(maskImg);
      
      /** Append Map to pane */
      Pane pane = new Pane();
      
      pane.setPrefHeight(roadHeight);
      pane.setPrefWidth(roadWidth);
      
      blackHole = new BlackHole(maskImg, roadWidth, roadHeight);
      pane.getChildren().addAll(mapView, maskView, racerPane, racersPane, blackHole);      
     
      map.getChildren().add(pane);
      toggleMask();
      
      /** Return root */
      return map;
   }
   
   public StackPane getMapRef() {
      return this.map;
   }
   
   /** Toggles the visibility of the mask (debugging). */
   public void toggleMask() {
      this.maskView.setOpacity(this.maskView.getOpacity()==0?255:0);
   }

   public void setDebug(String deb) {
      this.lblDebug.setText(deb);
   }
   
   public Label getFrame() {
      return this.lblDebug;
   }
   
   /** Updates the position of the racers for all racers to see. */
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
      
      /** Check for disconnected racers */
      for (String key:racers.keySet()) {
         if (!positions.containsKey(key)) {
            racersPane.getChildren().remove(racers.get(key)); 
            racers.remove(key);
         }
      }
   }
   
   /** Updates the leaderboard for all racers to see. */
   public void updateLeaderBoard() {
      Vector<Position> leaderboard = client.getLeaderBoard();
      
      String out = "";
      int position = 1;
      for (Position p:leaderboard) {
         out+=String.format("%d. %-20s LAPS: %3d\n",
            position++, p.getNickname(), p.getLaps()
            );
      }
      out += "\n";
      final String OUT = out;
      Platform.runLater(
            new Runnable() {
               public void run() {
                  leaderboardLabel.setText(OUT);
                  
               }
            }
         );
   }
   
   /** Updates the finish pane for all racers to see. */
   public void updateFinishPane() {
      Vector<Position> leaderboard = client.getLeaderBoard();
      
      String board = "";
      String message = (leaderboard.get(0).getNickname().equals(racer.getNickname())?"You Are The Winner!":"Better Luck Next Time...");
      int position = 1;
      for (Position p:leaderboard) {
         board+=String.format("%d. %-20s LAPS: %10d\n",
            position++, p.getNickname(), p.getLaps()
            );
      }
      
      board += "\n";
      final String BOARD = board;
      final String MESSAGE = message;
      Platform.runLater(
            new Runnable() {
               public void run() {
                  finishBoard.setText(BOARD);
                  finishMessage.setText(MESSAGE);
               }
            }
         );
   }
   
    /** Sets the text of the finish board. 
    * @param finishBoard A label value.
    */
   public void setFinishBoard(Label finishBoard) {
      this.finishBoard = finishBoard;
   }
   
   /** Sets the text of the finish message box.
    * @param finishMessage A label value.
    */
   public void setFinishMessage(Label finishMessage) {
      this.finishMessage = finishMessage;
   }
   
   /** Checks whether the racer is on the track or outside of it. */
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
      
      /** Restart if outside of map */
      if (racer.getPositionX()<0 || racer.getPositionY()<0) {
         racer.respawn();
      }
      if (racer.getPositionX()>this.roadWidth || racer.getPositionY()>this.roadHeight) {
         racer.respawn();
      }
   }
   
   /** Checks for the racers position on the track. */
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
            updateLaps(newPosition);
            oldPosition = newPosition;
         }
      } catch (Exception e) {
         
      }
   }
   
   /** Checks for the blackhole's position on the track. */
   public void checkForBlackHole() {
   
      double rectangle = blackHole.getWidth()/3;
      
      double x1 = blackHole.getPositionX()-rectangle;
      double y1 = blackHole.getPositionY()-rectangle;
      
      double x2 = blackHole.getPositionX()+rectangle;
      double y2 = blackHole.getPositionY()+rectangle;
      
      double rx = racer.getPositionX();
      double ry = racer.getPositionY();
      
      if ( ( x1<rx && rx<x2 ) && ( y1<ry && ry<y2 ) ) {
         racer.respawn();
      }
   }
   
    /** Updates the number of laps when the racer crosses the finish line. 
    * @param newPosition A double value.
    */
   public void updateLaps(double newPosition) {
   
      racer.setProgress(newPosition);
   
      /** Over finished, forward */
      if (isCloseTo(oldPosition, 1.0) && isCloseTo(newPosition, 0.0)) {
         racer.setLaps(racer.getLaps()+1); 
      }
      
      /** Over finnished, backward */
      if (isCloseTo(oldPosition, 0.0) && isCloseTo(newPosition, 1.0)) {
         racer.setLaps(racer.getLaps()-1);
      }
   }
   
   /** Used in updateLaps.
    * @param double The x position of the racer.
    * @param double The y position of the racer.
    * @return boolean Whether it is close or not.
    */
   public boolean isCloseTo(double x, double y) {
      return Math.abs(x-y)<0.2;
   }
   
   /** Sets the finish pane and it's visibility.
    * @param finishPane A Parent value.
    */
   public void setFinishPane(Parent finishPane) {
      this.finishPane = finishPane;
      this.finishPane.setVisible(false);
   }
   
     /** Sets the leaderboard label.
    * @param leaderboardLabel A Label value. 
    */
   public void setLeaderBoardLabel(Label leaderboardLabel) {
      this.leaderboardLabel = leaderboardLabel;
   }
   
    /** Sets the chat history.
    * @param chatLabel A Label value. 
    */
   public void setChatHistory(Label chatLabel) {
      this.chatLabel = chatLabel;
   }

      /** Updates the chat history so that its visible to all racers. */
   public void updateChatHistory() {
      Vector<String> chats = client.getChatHistory();
      String out = "";
      for (int i=1;i<(chats.size()<5?chats.size():5);i++) {
         out += chats.get(chats.size()-i) + "\n";
      }
      this.chatLabel.setText(out);
   }
   
 /** Handler */
   @Override public void handle(long timeStamp) {
      racer.update();
      blackHole.update();
      
      checkCollision();
      checkPosition();
      checkForBlackHole();
      
         new Thread() {
            public void run() {
               Platform.runLater(
                     new Runnable() {
                        public void run() {
                           updateRacers();
                        }
                     }
                  );
            }
         }.start();
         
      updateFinishPane();
      
      /** Show pane if race finished */
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