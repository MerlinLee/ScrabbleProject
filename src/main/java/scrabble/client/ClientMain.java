package scrabble.client;

import scrabble.client.clientControl.ClientControlCenter;

public class ClientMain {
    public static void main(String arg[]){
        new Thread(new ClientControlCenter()).start();
    }
}
