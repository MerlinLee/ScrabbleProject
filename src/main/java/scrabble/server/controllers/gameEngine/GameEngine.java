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

    private volatile static GameEngine gameEngine;
    public GameEngine(){}
    public static GameEngine gameEngine(){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine();
                }
            }
        }
        return gameEngine;
    }

    public static GameEngine gameEngine(BlockingQueue<ScrabbleProtocol> toEngine, BlockingQueue<ScrabbleProtocol> fromEngine){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine(toEngine, fromEngine);
                }
            }
        }
        return gameEngine;
    }

}
