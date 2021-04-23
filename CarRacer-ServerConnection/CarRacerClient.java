import java.io.*;
import java.util.*;
import java.net.*;

public class CarRacerClient extends Thread {
   /* Networking */
   private Socket socket = null;
   private ObjectOutputStream oos = null;
   private ObjectInputStream ois = null;
   
   // HashMap
   private HashMap<String, Position> positions = new HashMap<String, Position>();
   
   public CarRacerClient(String ipAddress, int port) {
      try{
      
         socket = new Socket(ipAddress, port);
         oos = new ObjectOutputStream(socket.getOutputStream());
         ois = new ObjectInputStream(socket.getInputStream());
      
      }catch(UnknownHostException uhe){
         uhe.printStackTrace();
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
   
   public void close() {
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
                        // READING
                        
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