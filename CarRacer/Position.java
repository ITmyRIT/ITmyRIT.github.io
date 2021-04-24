import java.io.Serializable;

// must implement Serializable in order to be sent
public class Position implements Serializable{

   // Attributes
   private final long serialVersionUID = 01L;
   private double positionX = 0;
   private double positionY = 0;
   private double rotation = 0;
   private int laps = -1;
   private String nickname = null;
   
   // Default Constructor
   public Position(){
      
   }
   
   // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation){
      this.positionX = positionX;
      this.positionY = positionY;
      this.rotation = rotation;
   }
   
   // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation, String nickname){
      this.positionX = positionX;
      this.positionY = positionY;
      this.rotation = rotation;
      this.nickname = nickname;
   }
   
   // Parameterized Constructor
   public Position(Position pos){
      this.positionX = pos.getPositionX();
      this.positionY = pos.getPositionY();
      this.rotation = pos.getRotation();
      this.nickname = pos.getNickname();
      this.laps = pos.getLaps();
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
   
   public void setLaps(int laps) {
      this.laps = laps;
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
   
   public String getNickname() {
      return this.nickname;
   }
   
   public int getLaps() {
      return this.laps;
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