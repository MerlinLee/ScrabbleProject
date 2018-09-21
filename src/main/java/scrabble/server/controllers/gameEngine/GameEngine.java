package scrabble.server.controllers.gameEngine;

import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.BlockingQueue;

public class GameEngine {
    private BlockingQueue<ScrabbleProtocol> fromCenter;
    private BlockingQueue<ScrabbleProtocol> toCenter;
    public GameEngine(BlockingQueue<ScrabbleProtocol> toEngine, BlockingQueue<ScrabbleProtocol> fromEngine) {
        this.fromCenter = toEngine;
        this.toCenter = fromEngine;
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
