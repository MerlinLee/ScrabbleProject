package scrabble.client.blockingqueue;

import scrabble.protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class GuiPutMsg implements Runnable{
    public GuiPutMsg(BlockingQueue<String> toCenter) {
        this.toCenter = toCenter;
    }

    private BlockingQueue<String> toCenter;
    @Override
    public void run() {
//        try {
//            toCenter.put(new Pack(1,""));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
