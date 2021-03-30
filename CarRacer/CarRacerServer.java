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
import java.net.*;
import java.util.*;

/*
 * Final Project - Car Racer Server
 * Authors: Mislav Breka, Karlo Longin, Marko Obsivac
 * CarRacer Class
 * Main GUI thread
 */

public class CarRacerServer extends Application {
   
   /* Window attributes */
   // Stage (window)
   private Stage stage;
   private Scene scene;
   private int sceneWidth = 600;
   private int sceneHeight = 300;
   
   // Network
   private ServerSocket sSocket = null;
   private static final int SERVER_PORT = 12345;

   // GUI
   private TextArea taLog = null;
   
   // Clients 
   ClientThread ct1;
   ClientThread ct2;
   
   /* Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /* Method called by launch(args) */
   @Override public void start(Stage stage) {
      this.stage = stage;
   
      stage.setTitle("Car Racer Server");
      stage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) {
               System.exit(0);
            }
         });
   
      updateScene(setupServer());
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
   public Parent setupServer() {
      // Create new root pane
      VBox root = new VBox(8);
      
      taLog = new TextArea();
      Button btnStart = new Button("Connect");
   
      btnStart.setPrefWidth(300);
      
      btnStart.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {        
               switch ( ( (Button)e.getSource() ).getText() ) {
                  case "Connect":
                     new ServerThread().start();
                     btnStart.setText("Disconnect");
                     break;
                  case "Disconnect":
                     btnStart.setText("Connect");
                     try{
                        sSocket.close();
                     }catch(IOException ioe){
                        ioe.printStackTrace();;
                     }
                     break;
               }
            }
         });
   
      root.setAlignment(Pos.CENTER);
      root.getChildren().addAll(btnStart, taLog);
      
      // Return root
      return root;
   }
   
   class ServerThread extends Thread {
      public void run() {
         try {
            sSocket = new ServerSocket(SERVER_PORT);
            //while(true) {
               ct1 = new ClientThread(sSocket.accept(), 1);
               ct2 = new ClientThread(sSocket.accept(), 2);
               ct1.start();
               ct2.start();
            //}
         } catch (IOException e) {
            e.printStackTrace();
         }
         
      }
   }
   
   class ClientThread extends Thread {
      private Socket socket = null;
      private int ID;
      
      // Streams
      private ObjectInputStream ois;
      private ObjectOutputStream oos;
        
      public ClientThread(Socket socket, int ID) {
         this.socket = socket;
         this.ID = ID;
         try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      @Override public void run(){
         while (true){
            
            Object input = receiveObject();
            
            
            if (input instanceof String) {
               if (((String)input).equals("STOP")) {
                  try {
                     oos.close();
                     ois.close();
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
               }
            }
            
            if(this.ID==1) ct2.sendObject(input);
            if(this.ID==2) ct1.sendObject(input);
         }
         
         
      }
      
      public void sendObject(Object object) {
         try {
            System.out.println("Sending: "+object);
            oos.writeObject(object);
            oos.flush();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      public Object receiveObject() {
         try {
            Object object = ois.readObject();
            System.out.println("Received: "+object);
            return object;
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }
   }
}
