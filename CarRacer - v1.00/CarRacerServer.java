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
   private ServerThread sThread = new ServerThread();
   private ServerSocket sSocket = null;
   private static final int SERVER_PORT = 12345;

   // GUI
   private TextArea taLog = null;
   
   // Clients
   private Vector<ClientThread> cThreads = new Vector<ClientThread>();
   private Hashtable<String, Position> cPositions = new Hashtable<String, Position>();
   
   // GameOver
   private boolean gameOver = false;
   private static final int NUM_OF_LAPS = 1;
   
   private Vector<Position> leaderboard = new Vector<Position>();
   
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
                     sThread.start();
                     btnStart.setText("Disconnect");
                     break;
                  case "Disconnect":
                     btnStart.setText("Connect");
                     
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
            while(true) {
            
               ClientThread ct = new ClientThread(sSocket.accept());
               ct.start();
               cThreads.add(ct);
            
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   class ClientThread extends Thread {
   
      private Socket socket = null;
      private ObjectInputStream ois = null;
      private ObjectOutputStream oos = null;
      private Position position = null;
      private String nickname = "";
      
      public ClientThread(Socket socket) {
         this.socket = socket;
         
         try {
         
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            
            nickname = (String) ois.readObject();
            Position position = (Position) ois.readObject();
            
            
            if (cPositions.containsKey(nickname)){
               System.out.println("Username already taken: "+nickname);
               nickname += String.format("%d", (int)(Math.random()*10));
               oos.writeObject("TAKEN");
               oos.writeObject(nickname);
            } else {
               oos.writeObject("OK");
            }
            
            oos.flush();
            
            cPositions.put(nickname, position);
            System.out.println("Connected: "+nickname);
            
         } catch (Exception e) {
            e.printStackTrace();
         } 
      }
   
      public void run() {
         while (true) {
            try {
               // READING AND WRITING
               position = (Position) ois.readObject();
               gameOver = position.getLaps() == NUM_OF_LAPS;
               updateLeaderBoard();
               
               cPositions.put(position.getNickname(), position);
               
               oos.writeObject(new Hashtable(cPositions)); 
               oos.flush();
               oos.writeObject(new Boolean(gameOver)); 
               oos.flush();
               oos.writeObject(new Vector(leaderboard)); 
               oos.flush();
            } catch (Exception e) {
               System.out.println("DISCONNECTED: "+position.getNickname());
               closeConnection();
               e.printStackTrace();
               break;
            }
         }
      }
      
      public void updateLeaderBoard() {
      
         Position[] toSort = new Position[cPositions.size()];
         
         int index = 0;
         for(String key:cPositions.keySet()) {
            toSort[index++] = cPositions.get(key);
         }
      
         // Sort players by laps and then by progress
         Arrays.sort(toSort, Collections.reverseOrder(
            new Comparator<Position>() {
               @Override
               public int compare(Position p1, Position p2) {
                  if (p1.getLaps() < p2.getLaps()) 
                     return -1;
                  else if (p1.getLaps() > p2.getLaps()) 
                     return 1;
                  else {
                     if (p1.getProgress() < p2.getProgress()) 
                        return -1;
                     else if (p1.getProgress() > p2.getProgress()) 
                        return 1;
                     else 
                        return 0;
                  }
               }
            }
            ));
      
         leaderboard = new Vector<Position>();
      
         for(Position p:toSort) {
            leaderboard.add(p);
         }
      
         String out = "";
         for (Position p:leaderboard) {
            out+=String.format("NAME: %-20s LAP: %3d PROG: %6.2f%%\n",
               p.getNickname(), p.getLaps(), p.getProgress()*100
               );
         }
         
         final String OUT = out;
         Platform.runLater(
            new Runnable() {
               public void run() {
                  taLog.setText(OUT);
               }
            }
            );
      }
      
      
      public void closeConnection() {
         cPositions.remove(position.getNickname());
         try{
            this.interrupt();
            socket.close();
            oos.close();
            ois.close();
         }catch(UnknownHostException uhe){
            uhe.printStackTrace();
         }catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
      
   }
}
