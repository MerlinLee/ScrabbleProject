package scrabble.server.controllers.net.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class NetPutMsg implements Runnable{
    private final BlockingQueue<String> toCenter;
    private final BlockingQueue<String> fromNetThread;
    private boolean flag = true;
    public NetPutMsg(BlockingQueue<String> toCenter,BlockingQueue<String> fromNetThread) {
        this.toCenter = toCenter;
        this.fromNetThread = fromNetThread;
    }

    @Override
    public void run() {
        while (flag){
            try {
                toCenter.put(fromNetThread.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown(){
        flag = false;
    }
}
