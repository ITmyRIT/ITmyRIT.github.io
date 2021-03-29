import java.io.*;
import java.util.*;
import java.net.*;

public class CarRacerClient {
   /* Networking */
   private Socket socket = null;
   ObjectOutputStream oos = null;
   ObjectInputStream ois = null;
         
   public CarRacerClient(String ipAddress, int port) {
      try{
         socket = new Socket(ipAddress, port);
         System.out.println("Connected!");
       
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
      
         oos.close();
         ois.close();
      }catch(UnknownHostException uhe){
         uhe.printStackTrace();
      }catch(IOException ioe){
         ioe.printStackTrace();
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