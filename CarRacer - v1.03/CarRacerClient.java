import java.io.*;
import java.util.*;
import java.net.*;

public class CarRacerClient extends Thread {
   /* Networking */
   private Socket socket = null;
   private ObjectOutputStream oos = null;
   private ObjectInputStream ois = null;
   
   // Hashtable
   private Hashtable<String, Position> positions = new Hashtable<String, Position>();
   
   private Racer racer = null;
   
   private boolean gameOver = false;
   
   private Vector<Position> leaderboard = new Vector<Position>();
   
   private String msgBuffer = "";
   private Vector<String> chatHistory = new Vector<String>();
   
   private boolean connected = false;
   
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
   
   public void run() {
      try {
        
            new Thread() {
               public void run() {
                  while (true) {
                     try {
                        // WRITING 
                     
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
                        // READING
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