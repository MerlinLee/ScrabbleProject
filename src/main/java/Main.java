import org.apache.log4j.PropertyConfigurator;
import scrabble.server.controllers.controlcenter.ControlCenter;

public class Main {
    public static void main(String[] args){
        PropertyConfigurator.configure(Main.class.getResourceAsStream("log4j.properties"));
        try {
            new Thread(new ControlCenter()).start();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
