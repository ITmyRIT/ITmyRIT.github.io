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
import java.awt.Color;

/*
 * Final Project - Car Racer 
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * CarRacer Class
 * Main GUI thread
 */

public class ClientTester extends Application {
   
   /* Window attributes */
   // Stage (window)
   private Stage stage;
   private Scene scene;
   private int sceneWidth = 1008;
   private int sceneHeight = 568;

   /* Race attributes */
   private Race race;
   
   /* Racer constants */
   private final double RACER_START_X = 0;
   private final double RACER_START_Y = 0;
   private final double RACER_START_DEG = 0;

   /* Map Constants */
   private static final String RACE_MAP = "assets/road.png";
   private static final String RACE_MASK = "assets/road-mask.png";
   
   /* Multiplayer Constants */
   private static final boolean MULTIPLAYER = true;
   private static final boolean SINGLEPLAYER = false;
   
   /* Server */
   private Socket socket;
   private int port = 12345;
   private String ip = "";
   private ObjectInputStream ois = null;
   private ObjectOutputStream oos = null;
   
   
   TextField tfX = new TextField("0");
   TextField tfY = new TextField("0");
   TextField tfR = new TextField("0");
   TextArea taServer = new TextArea();
   
   Position myPosition = new Position();
   
   /* Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /* Method called by launch(args) */
   @Override public void start(Stage stage) {
      this.stage = stage;
   
      stage.setTitle("Client Tester");
      stage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
               System.exit(0);
            }
         });
   
      updateScene(setupClient());
   }
   
   /* Method to display another scene */
   public void updateScene(Parent root) {
   
      // Create new scene
      scene = new Scene(root, sceneWidth, sceneHeight);
      
      // Update scene
      stage.setScene(scene);
      
      stage.setResizable(false);
      
      // Show stage
      stage.show();
   }
   
   /* Create and return the Menu GUI */
   public Parent setupClient() {
      // Create new root pane
      VBox root = new VBox(8);
   
      taServer.setPrefHeight(sceneHeight-100);
      
      root.setAlignment(Pos.CENTER);
      
      Button btnConnect = new Button("Connect");
      
      btnConnect.setOnAction(
         new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae){
               System.out.println("AAAAAAAAAAAAAAAAAAAAA");
               new RacerClient().start();
               btnConnect.setText("Disconnect");
            } 
         });
      
      root.getChildren().addAll(btnConnect, tfX, tfY, tfR, taServer);
   
      // Return root
      return root;
   }
   
   class RacerClient extends Thread {
      public void run() {
         try {
            socket = new Socket(ip, port); 
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());              
             
            int ID = (Integer) ois.readObject();
            myPosition.setID(ID);
            System.out.println("Received: " + ID);
            
               new Thread() {
                  public void run() {
                     while (true) {
                        try {
                           // READING
                           Position pos = (Position) ois.readObject(); 
                           taServer.appendText(pos.toString());
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                     }
                  }
               }.start();
            
               new Thread() {
                  public void run() {
                     while (true) {
                        try {
                           // WRITING 
                           synchronized(myPosition) {
                              myPosition.setPositionX(Double.parseDouble(tfX.getText()));
                              myPosition.setPositionY(Double.parseDouble(tfY.getText()));
                              myPosition.setRotation(Double.parseDouble(tfR.getText()));
                              System.out.println(myPosition);
                              
                              oos.writeObject(new Position(myPosition));
                              oos.flush();
                           }
                           //Thread.sleep(1000);
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                     }
                  }
               }.start();
            
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}