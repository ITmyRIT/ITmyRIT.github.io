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
   
   
   public CarRacerClient(String ipAddress, int port, Racer racer) {
      this.racer = racer;
   
      try{
      
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
         
      }catch(ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
      }catch(UnknownHostException uhe){
         uhe.printStackTrace();
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
   
  
   
   public void closeConnection() {
      try{
         socket.close();
         oos.close();
         ois.close();
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
                        oos.writeObject(new Position(racer.getPosition()));
                        oos.flush();
                        Thread.sleep(45);
                     } catch (Exception e) {
                        e.printStackTrace();
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
                     } catch (Exception e) {
                        e.printStackTrace();
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
}