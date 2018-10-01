package scrabble.server.controllers.net.blockingqueue;

import scrabble.protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class NetPutMsg implements Runnable{
    private final BlockingQueue<Pack> toCenter;
    private final BlockingQueue<Pack> fromNetThread;
    private boolean flag = true;
    public NetPutMsg(BlockingQueue<Pack> toCenter,BlockingQueue<Pack> fromNetThread) {
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
