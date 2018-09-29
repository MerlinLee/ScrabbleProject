package scrabble.server.controllers.gameEngine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.protocols.Package;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.server.controllers.gameEngine.blockingqueque.EngineGetMsg;
import scrabble.server.controllers.gameEngine.blockingqueque.EnginePutMsg;

import java.util.concurrent.*;

public class GameEngine implements Runnable{
    private BlockingQueue<Package> fromCenter;
    private BlockingQueue<Package> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    public GameEngine(BlockingQueue<Package> toEngine, BlockingQueue<Package> fromEngine) {
        this.fromCenter = toEngine;
        this.toCenter = fromEngine;
    }

    private void switchMessage(){
//        try {
//            //switch types of commands
//            //String tag = fromCenter.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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

    public static GameEngine getInstance(BlockingQueue<Package> toEngine, BlockingQueue<Package> fromEngine){
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
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(2,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new EngineGetMsg(fromCenter));
        pool.execute(new EnginePutMsg(toCenter));
    }

    public void shutdown(){
        flag = false;
    }
}
