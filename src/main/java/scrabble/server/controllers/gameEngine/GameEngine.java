package scrabble.server.controllers.gameEngine;

import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.BlockingQueue;

public class GameEngine implements Runnable{
    private BlockingQueue<ScrabbleProtocol> fromCenter;
    private BlockingQueue<ScrabbleProtocol> toCenter;
    private boolean flag = true;
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
    public static GameEngine getInstance(){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine();
                }
            }
        }
        return gameEngine;
    }

    public static GameEngine getInstance(BlockingQueue<ScrabbleProtocol> toEngine, BlockingQueue<ScrabbleProtocol> fromEngine){
        if (gameEngine == null ){
            synchronized (GameEngine.class){
                if (gameEngine == null){
                    gameEngine = new GameEngine(toEngine, fromEngine);
                }
            }
        }
        return gameEngine;
    }

    @Override
    public void run() {
        while (flag){
            try {
                fromCenter.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown(){
        flag = false;
    }
}
