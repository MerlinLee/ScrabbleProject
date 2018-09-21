package scrabble.server.controllers.net;

import java.util.concurrent.BlockingQueue;

public class NetCore {
    private final BlockingQueue<String> blockingQueue;
    private volatile boolean flag;

    public NetCore(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        flag=false;
    }

    //test put string
    public void netToGame(String message){
        try {
            blockingQueue.put(message);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
