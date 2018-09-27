package scrabble.server.controllers.gameEngine.blockingqueque;

import scrabble.protocols.Package;

import java.util.concurrent.BlockingQueue;

public class EngineGetMsg implements Runnable {
    public EngineGetMsg(BlockingQueue<Package> fromCenter) {
        this.fromCenter = fromCenter;
    }

    private BlockingQueue<Package> fromCenter;
    @Override
    public void run() {
        while (true){
            try {
                fromCenter.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
