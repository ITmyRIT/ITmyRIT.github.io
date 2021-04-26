//XML DOM PARSER
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.xml.sax.*;
import java.io.*;

public class LoadXML {
   // Default settings
   public String serverIP = "127.0.0.1";
   public int serverPort = 1234;
   public int numOfLaps = 3;
   
   private String fileName = "";
       
   public LoadXML(String fileName) {
      this.fileName = fileName;
      if (new File(fileName).exists()) {
         this.readXML();
      } else {
         this.writeXML();
      }
   }
   
   // Setters
   public void setServerIP(String serverIP) {
      this.serverIP = serverIP;
   }
   public void setServerPort(int serverPort) {
      this.serverPort = serverPort;
   }
   public void setNumOfLaps(int numOfLaps) {
      this.numOfLaps = numOfLaps;
   }
   
   // Getters
   public String getServerIP() {
      return this.serverIP;
   }
   public int getServerPort() {
      return this.serverPort;
   }
   public int getNumOfLaps() {
      return this.numOfLaps;
   }
   
   public void writeXML () {
      try {
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.newDocument();
         
         // here we create XML
         Element rootElement = doc.createElement("GameSettings");
         doc.appendChild(rootElement);
         Element socketElement = doc.createElement("Socket");
         rootElement.appendChild(socketElement);
         Element portElement =   doc.createElement("serverPort");
         portElement.appendChild(doc.createTextNode(this.serverPort+""));
         socketElement.appendChild(portElement);
         Element ipElement =   doc.createElement("serverIP");
         ipElement.appendChild(doc.createTextNode(this.serverIP));
         socketElement.appendChild(ipElement);
         
         Element gameElement = doc.createElement("Game");
         rootElement.appendChild(gameElement);
         Element lapElement = doc.createElement("gameEndLap");
         lapElement.appendChild(doc.createTextNode(this.numOfLaps+""));
         gameElement.appendChild(lapElement);
         
         // write the content into xml file
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         DOMSource source = new DOMSource(doc);
         StreamResult result = new StreamResult(new File(fileName));
         transformer.transform(source, result);
         
         // Output to console for testing
         StreamResult consoleResult = new StreamResult(System.out);
         transformer.transform(source, consoleResult);
      } catch (Exception e) {
         e.printStackTrace();
      }  
   }

   public void readXML(){
      try{
         DocumentBuilderFactory dbfactory= DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = dbfactory.newDocumentBuilder();
         XPathFactory xpfactory = XPathFactory.newInstance();
         XPath path = xpfactory.newXPath();
         
         File f = new File(this.fileName);
         Document doc = builder.parse(f);
            
         this.serverPort = Integer.parseInt(path.evaluate(
                  "/GameSettings/Socket/serverPort", doc));
                  
         this.serverIP = path.evaluate(
                  "/GameSettings/Socket/serverIP", doc);
                  
         this.numOfLaps = Integer.parseInt(path.evaluate(
                  "/GameSettings/Game/gameEndLap", doc));
         
         System.out.println("Readed XML:" + this.serverIP + ":" + this.serverPort + ":" + this.numOfLaps);         
      }
      catch(XPathExpressionException xpee){
         System.out.println(xpee);
      }
      catch(ParserConfigurationException pce){
         System.out.println(pce);
      }
      catch(SAXException saxe){
         System.out.println(saxe);
      }
      catch(IOException ioe){
         System.out.println(ioe);
      
      }
      
   }
}
   
