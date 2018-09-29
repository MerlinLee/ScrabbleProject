import org.apache.log4j.PropertyConfigurator;
import scrabble.server.controllers.controlcenter.ControlCenter;

public class Main {
    public static void main(String[] args){
        PropertyConfigurator.configure(Main.class.getResourceAsStream("log4j.properties"));
        new Thread(new ControlCenter()).start();

    }
}
