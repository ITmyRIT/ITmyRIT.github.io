import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

public class UseXML {
   public String fileName;
   
   public UseXML (String fileName) {
      this.fileName = fileName;
      
   }
   
   class Settings {
      public String IP;
      public int PORT;
      
      public Settings (String IP, int PORT) {
         this.IP = IP;
         this.PORT = PORT;
      }
   }
}