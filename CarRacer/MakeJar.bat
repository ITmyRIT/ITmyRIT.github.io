javac -g --module-path F:\ISTE-121\javafx-sdk-11.0.2\lib --add-modules=javafx.controls CarRacer.java *.java
jar -cmf mani.txt CarRacer.jar *.class *.png *.css 
jar -tvf CarRacer.jar
java --module-path F:\ISTE-121\javafx-sdk-11.0.2\lib --add-modules=javafx.controls -jar CarRacer.jar 
pause