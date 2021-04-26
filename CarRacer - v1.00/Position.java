import java.io.Serializable;

// must implement Serializable in order to be sent
public class Position implements Serializable{

   // Attributes
   private final long serialVersionUID = 01L;
   private double positionX = 0;
   private double positionY = 0;
   private double rotation = 0;
   private double opacity = 0;
   private double progress = 0;
   private int laps = 0;
   private String nickname = "";
   
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
      this(positionX, positionY, rotation);
      this.nickname = nickname;
   }
   
   // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity){
      this(positionX, positionY, rotation, nickname);
      this.opacity = opacity;
   }
   
    // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity, int laps){
      this(positionX, positionY, rotation, nickname, opacity);
      this.laps = laps;
   }
   
     
    // Parameterized Constructor
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity, int laps, double progress){
      this(positionX, positionY, rotation, nickname, opacity, laps);
      this.progress = progress;
   }
   
   // Parameterized Constructor
   public Position(Position pos){
      this.positionX = pos.getPositionX();
      this.positionY = pos.getPositionY();
      this.rotation = pos.getRotation();
      this.nickname = pos.getNickname();
      this.laps = pos.getLaps();
      this.opacity = pos.getOpacity();
      this.progress = pos.getProgress();
   }
   
   public boolean equals(Position p) {
      return Double.compare(this.getPositionX(), p.getPositionX()) == 0
          && Double.compare(this.getPositionY(), p.getPositionY()) == 0
          && Double.compare(this.getRotation(), p.getRotation()) == 0;
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
   
   public void setOpacity (double opacity) {
      this.opacity = opacity;
   }
   
   public void setProgress(double progress) {
      this.progress = progress;
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
   
   public double getOpacity() {
      return this.opacity;
   }
   
   public double getProgress() {
      return this.progress;
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