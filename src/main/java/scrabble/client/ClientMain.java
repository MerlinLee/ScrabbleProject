package scrabble.client;

import scrabble.client.clientControl.ClientControlCenter;

public class ClientMain {
    public static void main(String arg[]){
        try {
            new Thread(new ClientControlCenter()).start();
        }catch (Exception e){
            System.out.println("Try Again Please, my boy/girl!");
        }
    }
}
