import java.io.*;
import java.util.*;
import java.net.*;

public class CarRacerClient extends Thread {
   /* Networking */
   private Socket socket = null;
   ObjectOutputStream oos = null;
   ObjectInputStream ois = null;
   
   Racer racer = null;
   Racer racer2 = new Racer("");
   
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
         //System.out.println("Sending: "+object);
         oos.writeObject(object);
         oos.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Object receiveObject() {
      try {
         Object object = ois.readObject();
         //System.out.println("Received: "+object);
         return object;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }
   
   public void run() {
      
      while(true) {
      Position myPosition = new Position();
      myPosition.setPositionX(racer.getPositionX());
      myPosition.setPositionY(racer.getPositionY());
      myPosition.setRotation(racer.getRotation());
      this.sendObject(myPosition);
      
      Position opponentPosition = (Position)this.receiveObject();
      racer2.setPositionX(opponentPosition.getPositionX());
      racer2.setPositionY(opponentPosition.getPositionY());
      racer2.setRotation(opponentPosition.getRotation());
      
      System.out.println(opponentPosition.getPositionX());
      System.out.println(opponentPosition.getPositionY());
      }
   }
   
   public Racer getRacer() {
      return this.racer2;
   }
   
   public void setRacer(Racer racer) {
      this.racer = racer;
   }
}