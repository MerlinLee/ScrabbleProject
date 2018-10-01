package scrabble.client.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class GuiPutMsg implements Runnable{
    public GuiPutMsg(BlockingQueue<Package> toCenter) {
        this.toCenter = toCenter;
    }

    private BlockingQueue<Package> toCenter;
    @Override
    public void run() {
        try {
            toCenter.put(new Package(1,""));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
