import java.io.Serializable;

// must implement Serializable in order to be sent
public class Position implements Serializable{

   // Attributes
   private final long serialVersionUID = 01L;
   private double positionX = 0;
   private double positionY = 0;
   private double rotation = 0;
   
   // Default Constructor
   public Position(){
      
   }
   
   // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation){
      this.positionX = positionX;
      this.positionY = positionY;
      this.rotation = rotation;
   }
   
   
   // Setters
   public void setPositionX(double positionX){
      this.positionX = positionX;
   }
   
   public void setPositionY(double positionY){
      this.positionY = positionY;
   }
   
   public void setRotation(double rotation){
      this.rotation = rotation;
   }
   
   // Getters
   public double getPositionX(){
      return this.positionX;
   }
   
   public double getPositionY(){
      return this.positionY;
   }
   
   public double getRotation(){
      return this.rotation;
   }
   
   public String toString(){
      return String.format(
         "%s\nX: %f\nY: %f\n R: %f\n",
         "*".repeat(10),
         this.getPositionX(),
         this.getPositionY(),
         this.getRotation()
      );
   }
   
}