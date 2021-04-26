import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;

import javafx.scene.text.*;//font support

/**
 * GUIStarter - class to help with JavaFX classes
 * @author  D. Patric
 * @version 2205
 */

public class StopWatch extends Application implements EventHandler<ActionEvent> {
   // Attributes are GUI components (buttons, text fields, etc.)
   // are declared here.
   private Stage stage;        // The entire window, including title bar and borders
   private Scene scene;        // Interior of window
   // Choose a pane ... VBox used here
   private VBox root = new VBox(8);
   
   TextField tfTime;
   Button btnStart;
   
   private long currentTime = 0L;
   private java.util.Timer timer = null;
   
   // Main just instantiates an instance of this GUI class
   public static void main(String[] args) {
      launch(args);
   }
   
   // Called automatically after launch sets up javaFX
   public void start(Stage _stage) throws Exception {
      stage = _stage;                        // save stage as an attribute
      stage.setTitle("GUI Starter");            // set the text in the title bar
      
      scene = new Scene(root, 300, 150);   // create scene of specified size 
                                             // with given layout
      
      //textField for Time
      tfTime = new TextField();
      tfTime.setAlignment(Pos.CENTER);
      tfTime.setText("10");
      Font currentFont = tfTime.getFont();
      tfTime.setFont(Font.font(currentFont.getName(), FontWeight.BOLD,48));
      root.getChildren().add(tfTime);
      
      //button
      FlowPane fpButton = new FlowPane();
      btnStart = new Button("Start");
      fpButton.setAlignment(Pos.CENTER);
      fpButton.getChildren().add(btnStart);
      root.getChildren().add(fpButton);
      
      //events support
      btnStart.setOnAction(this);
      
      stage.setScene(scene);                 // associate the scene with the stage
      stage.show();                          // display the stage (window)
   }
   
   private void doStart(){
      currentTime = 10L;
      tfTime.setText("00");
      
      timer = new java.util.Timer();
      timer.scheduleAtFixedRate(new MyTimerTask(),0,1000);
      btnStart.setText("Stop");
   }   
   
   private void doStop(){
      timer.cancel();
      timer = null;
      
   }
   
   class MyTimerTask extends java.util.TimerTask {
      public void run() {
         currentTime -= 1;
         
         tfTime.setText(String.format("%02d", currentTime));
         
         if(currentTime == 0L){
            doStop();
         }
      }
   }
   
   public void handle(ActionEvent evt) {
      // Get the button that was clicked
      Button btn = (Button)evt.getSource();
      
      // Switch on its name
      switch(btn.getText()) {
         case "Start":
            System.out.println("START");
            doStart();
            break;
         case "Stop":
            System.out.println("STOP");
            doStop();
            break;
      }
   }   
}	
