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

/**
 * Final Project - Car Racer 
 * @author Mislav Breka 
 * @author Karlo Longin
 * @author Marko Obsivac
 * CarRacerServer Class
 * The server on which the clients connect to to share the gameplay data.
 */

public class CarRacerServer extends Application {
   
   /* Window attributes */
   /** Stage (window) */
   private Stage stage;
   private Scene scene;
   private int sceneWidth = 600;
   private int sceneHeight = 300;
   
   /** Network */
   private ServerThread sThread = null;
   private ServerSocket sSocket = null;
   private int serverPort = 12345;

   /** GUI */
   private TextArea taConnection = null;
   private TextArea taClients = null;
   
   /** Clients */
   private Vector<ClientThread> cThreads = new Vector<ClientThread>();
   private Hashtable<String, Position> cPositions = new Hashtable<String, Position>();
   
   /** GameOver */
   private Boolean gameOver = false;
   private int numOfLaps = 1;
   
   private Vector<Position> leaderboard = new Vector<Position>();
   private Vector<String> messageLog = new Vector<String>();
   
   /** XML Settings */
   private static final String XML_PATH = "ServerConfig.xml";

   /** Main method */
   public static void main(String[]args) {
      launch(args);
   }
   
   /** Method called by launch(args) */
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
   
   /** Method to display another scene */
   public void updateScene(Parent root) {
   
      /** Create new scene */
      scene = new Scene(root, sceneWidth, sceneHeight);
      
      /** Update scene*/
      stage.setScene(scene);
      
      stage.setResizable(false);
      
      /** Show stage*/
      stage.show();
   }
   
   /** Create and return the Menu GUI */
   public Parent setupServer() {
      LoadXML xml = new LoadXML(XML_PATH);
      this.serverPort = xml.getServerPort();
      this.numOfLaps = xml.getNumOfLaps();
      
      /** Create new root pane*/
      VBox root = new VBox(8);
      HBox areaBox = new HBox();
      
      taConnection = new TextArea();
      taClients = new TextArea();
      
      /** Button Style*/
      Button btnStart = new Button("Start");
      btnStart.setStyle("-fx-font: 22 montserrat; -fx-base: #000000;-fx-text-fill: white;-fx-border-color:#42f5ef;-fx-border-width: 2 2 2 2; -fx-border-radius: 20px; -fx-background-radius: 20px;");
      btnStart.setPrefWidth(300);
      
      /** btnStart Action*/
      btnStart.setOnAction(
         new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {        
               switch ( ( (Button)e.getSource() ).getText() ) {
                  case "Start":
                     taConnection.appendText("Open for client connections!\n");
                     sThread = new ServerThread();
                     sThread.start();
                     btnStart.setText("Stop");
                     break;
                  case "Stop":
                     taConnection.appendText("Closed for client connections :(\n");
                     btnStart.setText("Start");
                     sThread.stop();
                     try{sSocket.close();}
                     catch(Exception x){}
                     disconnectAllClients();
                     break;
               }
            }
         });
   
      /** Root Style*/
      root.setAlignment(Pos.CENTER);
      root.setStyle("-fx-background-color: #000000;");
      
      /** AreaBox Style*/
      areaBox.setStyle("-fx-base: #000000;-fx-text-fill: white;-fx-border-color:#42f5ef;-fx-border-width: 2 2 2 2;");
      
      /** Add to root*/
      areaBox.getChildren().addAll(taConnection, taClients);
      root.getChildren().addAll(btnStart, areaBox);
      
      /** Return root*/
      return root;
   }
   
   public void disconnectAllClients() {
      for (ClientThread ct:cThreads) {
         ct.closeConnection();
      }
      cThreads = new Vector<ClientThread>();
      cPositions = new Hashtable<String, Position>();
   }
   
   class ServerThread extends Thread {
      public void run() {
         try {
            sSocket = new ServerSocket(serverPort);
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
      private String message = "";
      
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
               taClients.appendText(nickname + " tried to use a username that's already taken.");
            } else {
               oos.writeObject("OK");
            }
            
            oos.flush();
            
            cPositions.put(nickname, position);
            System.out.println("CONNECTED: "+nickname);
            taConnection.appendText("CONNECTED: "+nickname + "\n");
            
         } catch (Exception e) {
            e.printStackTrace();
         } 
      }
   
      public void run() {
         while (true) {
            try {
            
            
               /** READING AND WRITING*/
               
               String cmd = (String) ois.readObject();
               
               switch (cmd) {
                  case "POSITION":
                     position = (Position) ois.readObject();
                     break;
                  case "CHAT":
                     message = (String) ois.readObject();
                     break;
               }
               
               
               
               if(!message.trim().equals("")){
                  System.out.println(String.format("%10s: %30s",nickname,message));
                  messageLog.add(String.format("%-8s: %23s",nickname,message));
                  message = "";
               }
               
               gameOver = checkIfGameOver();
               updateLeaderBoard();
               
               cPositions.put(position.getNickname(), position);
               
               oos.writeObject(new Hashtable(cPositions)); 
               oos.flush();
               oos.writeObject(new Boolean(gameOver)); 
               oos.flush();
               synchronized (leaderboard) {
                  oos.writeObject(new Vector(leaderboard)); 
                  oos.flush();
               }
               synchronized (messageLog) {
                  oos.writeObject(new Vector(messageLog)); 
                  oos.flush();
               }
            } catch (Exception e) {
               System.out.println("DISCONNECTED: "+position.getNickname());
               taConnection.appendText("DISCONNECTED: "+position.getNickname()+ "\n");
               closeConnection();
               break;
            }
         }
      }
      
      public boolean checkIfGameOver() {
         for(String key:cPositions.keySet()) {
            if (cPositions.get(key).getLaps() == numOfLaps) 
               return true;
         }
         return false;
      }
      
      public void updateLeaderBoard() {
      
         Position[] toSort = new Position[cPositions.size()];
         
         int index = 0;
         for(String key:cPositions.keySet()) {
            toSort[index++] = cPositions.get(key);
         }
      
         /** Sort players by laps and then by progress*/
         Arrays.sort(toSort, 
            new Comparator<Position>() {
               @Override
               public int compare(Position p1, Position p2) {
                  if (p1.getLaps() < p2.getLaps()) 
                     return 1;
                  else if (p1.getLaps() > p2.getLaps()) 
                     return -1;
                  else {
                     if (p1.getProgress() < p2.getProgress()) 
                        return 1;
                     else if (p1.getProgress() > p2.getProgress()) 
                        return -1;
                     else 
                        return 0;
                  }
               }
            });
      
         leaderboard = new Vector<Position>();
      
         for(Position p:toSort) {
            leaderboard.add(p);
         }
      
         String out = "";
         synchronized (leaderboard) {
            for (Position p:leaderboard) {
               out+=String.format("NAME: %-20s LAP: %3d PROG: %6.2f%%\n",
                  p.getNickname(), p.getLaps(), p.getProgress()*100
                  );
            }
         }
         
         final String OUT = out;
         Platform.runLater(
            new Runnable() {
               public void run() {
                  taClients.setText(OUT);
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
