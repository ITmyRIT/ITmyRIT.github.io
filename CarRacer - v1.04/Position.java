import java.io.Serializable;

// 
/** Represents the position of a Racer, implements Serializable in order to be sent.
     * @author Mislav Breka
     * @author Karlo Longin
     * @version 1.05
     * @since 1.0
   */
public class Position implements Serializable{

   /** Attributes */
   
   /** For sending over the network. */
   private final long serialVersionUID = 01L;
   
   /** Racer's attributes. */
   private double positionX = 0;
   private double positionY = 0;
   private double rotation = 0;
   private double opacity = 0;
   private double progress = 0;
   private int laps = 0;
   private String nickname = "";
   
   /** Default constructor */
   public Position(){
      
   }
   
   /** Stores the racers position and angle. 
     * @param positionX The racer’s position on the abscissa.
     * @param positionX The racer’s position on the ordinate.
     * @param rotation The racer’s current angle.
     */
   public Position(double positionX, double positionY, double rotation){
      this.positionX = positionX;
      this.positionY = positionY;
      this.rotation = rotation;
   }
   
   /** Stores the racers position, angle, and nickname. 
     * @param positionX The racer’s position on the abscissa.
     * @param positionX The racer’s position on the ordinate.
     * @param rotation The racer’s current angle.
     * @param rotation The nickname which was assigned by the user.
     */
   public Position(double positionX, double positionY, double rotation, String nickname){
      this(positionX, positionY, rotation);
      this.nickname = nickname;
   }
   
   /** Stores the racers position, angle, nickname, and opacity. 
     * @param positionX The racer’s position on the abscissa.
     * @param positionX The racer’s position on the ordinate.
     * @param rotation The racer’s current angle.
     * @param nickname The nickname which was assigned by the user.
     * @param opacity The opacity of the car image.
     */
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity){
      this(positionX, positionY, rotation, nickname);
      this.opacity = opacity;
   }
   
    /** Stores the racers position, angle, nickname, opacity, and laps. 
     * @param positionX The racer’s position on the abscissa.
     * @param positionX The racer’s position on the ordinate.
     * @param rotation The racer’s current angle.
     * @param nickname The nickname which was assigned by the user.
     * @param opacity The opacity of the car image.
     * @param laps The number of laps that the racer fulfiled.
     */
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity, int laps){
      this(positionX, positionY, rotation, nickname, opacity);
      this.laps = laps;
   }
     
    /** Stores the racers position, angle, nickname, opacity, laps, and its current progress on the map. 
     * @param positionX The racer’s position on the abscissa.
     * @param positionX The racer’s position on the ordinate.
     * @param rotation The racer’s current angle.
     * @param nickname The nickname which was assigned by the user.
     * @param opacity The opacity of the car image.
     * @param laps The number of laps that the racer has fulfiled.
     */
   public Position(double positionX, double positionY, double rotation, String nickname, double opacity, int laps, double progress){
      this(positionX, positionY, rotation, nickname, opacity, laps);
      this.progress = progress;
   }
   
    /** Stores the Position class of the racer. 
     * @param pos An instance of the Position class from a racer. 
     */
   public Position(Position pos){
      this.positionX = pos.getPositionX();
      this.positionY = pos.getPositionY();
      this.rotation = pos.getRotation();
      this.nickname = pos.getNickname();
      this.laps = pos.getLaps();
      this.opacity = pos.getOpacity();
      this.progress = pos.getProgress();
   }
   
   /** Takes the position of a racer and compares the positions on x, y, and the rotation. 
    *@param p An instance of the Position class from a racer.
    *@return A boolean which is true if everything is at 0.
    */
   public boolean equals(Position p) {
      return Double.compare(this.getPositionX(), p.getPositionX()) == 0
          && Double.compare(this.getPositionY(), p.getPositionY()) == 0
          && Double.compare(this.getRotation(), p.getRotation()) == 0;
   }
   
   
   /** Setters */
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
   
   /** toString that displays the racer's position and rotation.
     * @return A formatted String with the the racer's position and rotation.
   */
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