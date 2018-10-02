package scrabble.client.Net.blockingqueue;

import scrabble.protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class clientNetPutMsg implements Runnable {
    private final BlockingQueue<String> toCenter;
    private final BlockingQueue<String> fromNetThread;
    private boolean flag = true;
    public clientNetPutMsg(BlockingQueue<String> toCenter,BlockingQueue<String> fromNetThread) {
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
