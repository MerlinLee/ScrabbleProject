package scrabble.server.controllers.gameEngine;

import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameEngine implements Runnable{
    private BlockingQueue<ScrabbleProtocol> fromCenter;

    public static BlockingQueue<ScrabbleProtocol> getToCenter() {
        return new LinkedBlockingQueue<>();
    }

    private BlockingQueue<ScrabbleProtocol> toCenter;
    public GameEngine(BlockingQueue<ScrabbleProtocol> fromCenter) {
        this.fromCenter = fromCenter;
    }

    @Override
    public void run() {
        switchMessage();
    }

    private void switchMessage(){
        try {
            //switch types of commands
            String tag = fromCenter.take().getTAG();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
