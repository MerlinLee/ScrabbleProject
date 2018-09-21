package scrabble.server.controllers.game;

import java.util.concurrent.BlockingQueue;

public class GameCore {
    public GameCore(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        flag = false;
    }

    private final BlockingQueue<String> blockingQueue;
    private volatile boolean flag;
}
