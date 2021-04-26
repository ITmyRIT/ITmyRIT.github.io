import java.io.*;
import java.util.*;
import java.net.*;


/**
 * Final Project - Car Racer 
 * @author Mislav Breka 
 * @author Karlo Longin
 * @author Marko Obsivac
 * CarRacerClient Class
 * Used for connecting the client to the server.
 */
public class CarRacerClient extends Thread {
   /** Networking */
   private Socket socket = null;
   private ObjectOutputStream oos = null;
   private ObjectInputStream ois = null;
   
   /** Hashtable */
   private Hashtable<String, Position> positions = new Hashtable<String, Position>(); 
   private Racer racer = null;
   
   /** Leaderboard */
   private Vector<Position> leaderboard = new Vector<Position>();
   
   /** Chat attributes */
   private String msgBuffer = "";
   private Vector<String> chatHistory = new Vector<String>();
   
   /** Booleans */
   private boolean connected = false;
   private boolean gameOver = false;
   
   /** Creates a new client which accepts the ipAddress, port, racer, and throws a general Exception.
    * @param ipAddress A String value which stores the ipAddress.
    * @param int A int value which stores the port.
    * @param racer A Racer value which stores the racer.
    */
   public CarRacerClient(String ipAddress, int port, Racer racer) throws Exception {
      this.racer = racer;
      
      connected = true;
    
      socket = new Socket(ipAddress, port);
      oos = new ObjectOutputStream(socket.getOutputStream());
      ois = new ObjectInputStream(socket.getInputStream());
         
         // Writing nickname
      oos.writeObject(racer.getNickname());
      oos.flush();
         
         // Writing position
      oos.writeObject(new Position(racer.getPosition()));
      oos.flush();
         
         // Checking if username OK or not
      String reply = (String) ois.readObject();
      if (reply.equals("TAKEN")) {
         String newNickname = (String) ois.readObject();
         racer.setNickname(newNickname);
      } 
   }
   
   /** Disconnects from the server */
   public void closeConnection() {
      try{
         connected = false;
         socket.close();
         oos.close();
         ois.close();
         socket = null;
         oos = null;
         ois = null;
      }catch(UnknownHostException uhe){
         uhe.printStackTrace();
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
   /** The run method since the class is a Thread */
   public void run() {
      try {
        
            new Thread() {
               public void run() {
                  while (true) {
                     try {
                        /** WRITING */ 
                     
                        synchronized (oos) {
                           oos.writeObject("POSITION");
                           oos.writeObject(new Position(racer.getPosition()));
                           oos.flush();
                        }
                     
                        Thread.sleep(45);
                     } catch (Exception e) {
                        e.printStackTrace();
                        closeConnection();
                        break;
                     }
                  }
               }
            }.start();
            new Thread() {
               public void run() {
                  while (true) {
                     try {
                        /** READING */
                        positions = (Hashtable<String, Position>) ois.readObject();
                        gameOver = (Boolean) ois.readObject();
                        leaderboard = (Vector<Position>) ois.readObject();
                        chatHistory = (Vector<String>) ois.readObject();
                        
                     } catch (Exception e) {
                        e.printStackTrace();
                        closeConnection();
                        break;
                     }
                  }
               }
            }.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public Hashtable<String, Position> getPositions() {
      return positions;
   }
   
   public boolean getGameOver() {
      return this.gameOver;
   }
   
   public Vector<Position> getLeaderBoard() {
      return this.leaderboard;
   }

   /** Sends the message to the server
    * @param message The message that the client wants to send.
    */
   public void sendChat(String message) throws Exception {
      synchronized(oos) {
         oos.writeObject("CHAT");
         oos.writeObject(new String(message));
      
         oos.flush();
      
      }
   }
   
   public Vector<String> getChatHistory() {
      return chatHistory;
   }
   
   public boolean isConnected() {
      return connected;
   }  
}