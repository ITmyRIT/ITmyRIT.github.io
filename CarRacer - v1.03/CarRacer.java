import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

/*
 * Final Project - Car Racer 
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * CarRacer Class
 * Main GUI thread
 */

public class CarRacer extends Application {
   
   /* Window attributes */
   // Stage (window)
   private Stage stage;
   private Scene scene;
   private int sceneWidth = 1008;
   private int sceneHeight = 568;

   /* Race attributes */
   private Race race;
   
   /* Racer constants */
   private final double RACER_START_X = 34;
   private final double RACER_START_Y = 304;
   private final double RACER_START_DEG = -288;

   /* Map Constants */
   private static final String RACE_MAP = "assets/road.png";
   private static final String RACE_MASK = "assets/road-mask-green-red.png";
   private static final String RACE_POSITION = "assets/road-mask-position.png";
   
   /* Player */
   private String nickname = "";
   private boolean wasActive = false;

   /* TextField for chat */
   private TextField tfChat = new TextField();
   
   /* Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /* Method called by launch(args) */
   @Override public void start(Stage stage) {
      this.stage = stage;
   
      stage.setTitle("Space Racer");
      stage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
               System.exit(0);
            }
         });
   
      updateScene(setupMenu());
   }
   
   /* Method to display another scene */
   public void updateScene(Parent root) {
   
      // Loading screen
      loadingScreen();
   
      // Create new scene
      scene = new Scene(root, sceneWidth, sceneHeight);
      
      // Start listening to key events
      handleKeys();
      
      // Update scene
      stage.setScene(scene);
      
      stage.setResizable(false);
      
      // Show stage
      stage.show();
   }
   
   /* Create and return the Menu GUI */
   public Parent setupMenu() {
      // Create new root pane
      VBox root = new VBox(8);
      Image image = null;
      
      // Creating an image 
      try{
         image = new Image(new FileInputStream("assets/logo.png"));
      }
      catch(FileNotFoundException fnfe){
         fnfe.printStackTrace();
      }  
      ImageView imageView = new ImageView(image); 
      
      TextField tfNick = new TextField();
      
      // Instantiate buttons
      Button btnPlay = new Button("Play");
      
      // Assign fonts
      Font font = Font.loadFont("assets/font/Montserrat-Bold.ttf", 45);
      btnPlay.setFont(font);
      tfNick.setFont(font);
      
      // CSS Style
      btnPlay.setStyle("-fx-font: 22 montserrat; -fx-base: #000000;-fx-text-fill: white;-fx-border-color:#42f5ef;-fx-border-width: 2 2 2 2; -fx-border-radius: 20px; -fx-background-radius: 20px;");
      tfNick.setStyle("-fx-font: 22 montserrat; -fx-base: #000000;-fx-text-fill: white;-fx-border-color:#42f5ef;-fx-border-width: 2 2 2 2; -fx-border-radius: 20px; -fx-background-radius: 20px;");
      root.setStyle("-fx-background-color: #000000;");
      
      // Width of buttons
      btnPlay.setPrefWidth(300);
      tfNick.setMaxWidth(300);
      tfNick.setAlignment(Pos.CENTER);
         
      btnPlay.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               nickname = tfNick.getText();
               if (nickname.length()>8) {
                  tfNick.setText(nickname.substring(0,8));
                  return;
               }
               updateScene(setupGame());
            }
         });
         
      root.setAlignment(Pos.CENTER);
      root.getChildren().addAll(imageView, tfNick, btnPlay);
      
      // Return root
      return root;
   }
   
      
   /* Create and return the MultiPlayer Race GUI */
   public Parent setupGame() {
      // Create new root pane
      StackPane root = new StackPane();
      
      
      // Initialize Race
      race = new Race(RACE_MAP, RACE_MASK, RACE_POSITION, sceneWidth, sceneHeight);
      
      // Initialize Racer
      Racer racer = new Racer(nickname, RACER_START_X, RACER_START_Y, RACER_START_DEG);
      //Racer racer = new Racer(nickname);
   
      race.setRacer(racer);
      
      
      // Try connecting to server
      if (!race.connectToServer()) {
         System.out.println("AAAA COULD NOT CONNECT ://///");
         return setupMenu();
      }
   
      root.getChildren().add(race.getMap());
      
      // Add finish screen
      Parent bpRace = finishScreen();
      
      race.setFinishPane(bpRace);
      
      root.getChildren().add(bpRace);
      
      // Leaderboard
      VBox leadBox = new VBox();
      Label lblLeaderboard = new Label();
      
      lblLeaderboard.setFont(Font.font(java.awt.Font.MONOSPACED, lblLeaderboard.getFont().getSize()));
   
      race.setLeaderBoardLabel(lblLeaderboard);
      lblLeaderboard.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.7), new CornerRadii(5.0), new Insets(-4, -4, -4, -4))));
      leadBox.setAlignment(Pos.TOP_LEFT);
      leadBox.setMargin(lblLeaderboard, new Insets(0, 0, 0, sceneWidth/4));
      leadBox.getChildren().add(lblLeaderboard);
      
      root.getChildren().add(leadBox);
      
      // Chat
      VBox chatBox = new VBox();
      Label lblChat = new Label();
      lblChat.setMaxWidth(sceneWidth/4);
      lblChat.setFont(Font.font(java.awt.Font.MONOSPACED, lblChat.getFont().getSize()));
   
      race.setChatHistory(lblChat);
      
      lblChat.setWrapText(true);
      tfChat = new TextField();
      
      tfChat.setMaxWidth(sceneWidth/4);
      lblChat.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.7), new CornerRadii(5.0), new Insets(-4, -4, -4, -4))));
      tfChat.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.7), new CornerRadii(5.0), new Insets(-4, -4, -4, -4))));
      tfChat.setStyle("-fx-border-color:#ffffff00;");
      chatBox.setAlignment(Pos.BOTTOM_RIGHT);
      chatBox.setMargin(lblChat, new Insets(0, sceneWidth/4, 0, 0));
      chatBox.setMargin(tfChat, new Insets(10, sceneWidth/4, 0, 0));
      chatBox.getChildren().addAll(lblChat, tfChat);
      root.getChildren().add(chatBox);
      
      
      // Start race
      race.start();
   
      // Return root
      return root;
      
   }
   
    /* Create and return the SinglePlayer Race GUI */
   public Parent loadingScreen() {
      // Create new root pane
      VBox root = new VBox(8);
   
      root.setAlignment(Pos.CENTER);
      
      // Return root
      return root;
   }
   
   /* Create and return the SinglePlayer Race GUI */
   public Parent finishScreen() {
      // Create new root pane
      BorderPane root = new BorderPane();
   
      VBox message = new VBox(8);
      message.setBackground(new Background(
         new BackgroundFill(
            Color.rgb(0,0,0,0.8),
            new CornerRadii(5), 
            null)));
      message.setMaxWidth(sceneWidth/2);
      message.setMaxHeight(sceneHeight/2);
   
      message.setPadding(new Insets(10,10,10,10));
      
      Label lblFinishMessage = new Label();
      Label lblFinishBoard = new Label();
      
      lblFinishMessage.setStyle("-fx-font: 30 montserrat;-fx-text-fill: cyan;");
      lblFinishBoard.setStyle("-fx-font: 20 montserrat;-fx-text-fill: white;");
      lblFinishBoard.setFont(Font.font(java.awt.Font.MONOSPACED, lblFinishBoard.getFont().getSize()));
      race.setFinishBoard(lblFinishBoard);
      race.setFinishMessage(lblFinishMessage);
      
      message.getChildren().addAll(lblFinishMessage, lblFinishBoard);
      message.setAlignment(Pos.CENTER);
      root.setCenter(message);
      // Return root
      return root;
   }

      
   /* Debugger */
   public void showDebugger() {
      VBox root = new VBox();
      Label lblFPS = race.getFrame();
      Button btnRotate = new Button("Rotate");
      Button btnBackToStart = new Button("Start Position");
      Button btnMapMask = new Button("Toggle Map Mask");
      Button btnRacerMask = new Button("Toggle Racer Mask");
      btnRotate.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               if (race.getRacer().isRotating()) {
                  race.getRacer().rotate(false);
               } else {
                  race.getRacer().rotate(true);
               }
            }
         });
      btnBackToStart.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               race.getRacer().setPositionX(RACER_START_X);
               race.getRacer().setPositionY(RACER_START_Y);
               race.getRacer().setRotation(RACER_START_DEG);
            }
         });
      btnMapMask.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               race.toggleMask();
            }
         });
      btnRacerMask.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
               race.getRacer().toggleMask();
            }
         });
      root.getChildren().addAll(lblFPS, btnRotate, btnBackToStart, btnMapMask, btnRacerMask);
      Stage stage = new Stage();
      stage.setTitle("Debug");
      stage.setScene(new Scene(root, 300, 300));
      stage.show();
   }
   
   
   
   /* Keyboard Handler */
   public void handleKeys() {
      // Create racer
      Racer racer;
      
      // If race does not exist then return from method
      try {
         racer = race.getRacer();
         race.getMapRef().requestFocus();
      } catch (NullPointerException e) {
         return;
      }
      
      /* KEY PRESSED */
      scene.addEventFilter(KeyEvent.KEY_PRESSED, 
         key -> {
         
            switch (key.getCode()) {
               case UP: case W:
                  racer.goFoward(true);
                  break;
               case DOWN: case S:
                  racer.goBackward(true);
                  break;
               case LEFT: case A:
                  racer.goLeft(true);
                  break;
               case RIGHT: case D:
                  racer.goRight(true); 
                  break;
               case SHIFT:
                  racer.goTurbo(true);
                  break;
               case F3:
                  showDebugger();
                  break;
               case ESCAPE:
                  race.getClient().closeConnection();
                  updateScene(setupMenu());
                  race.stop();
                  break;
               case TAB:
                  wasActive = true;
                  race.getRacer().setKeyboard(false);
                  tfChat.requestFocus(); 
                  break;
                   
            }
         });
             
      /* KEY RELEASED */
      scene.addEventFilter(KeyEvent.KEY_RELEASED, 
         key -> {
         
            switch (key.getCode()) {
               case UP: case W:
                  racer.goFoward(false);
                  break;
               case DOWN: case S:
                  racer.goBackward(false);
                  break;
               case LEFT: case A:
                  racer.goLeft(false);
                  break;
               case RIGHT: case D:
                  racer.goRight(false);
                  break;
               case SHIFT: 
                  racer.goTurbo(false);
                  break;
               case TAB:
                  if (wasActive) wasActive = false;
                  else 
                     return;
                  String msg = tfChat.getText();
                  tfChat.clear();
                  try {
                     race.getClient().sendChat(msg);
                  } catch (Exception e) {}
                  race.getRacer().setKeyboard(true);
                  race.getMapRef().requestFocus();
                  break;
               case ENTER:
                 
                  break;
            }
         });
   }
}