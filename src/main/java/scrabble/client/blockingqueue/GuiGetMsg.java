package scrabble.client.blockingqueue;

import java.util.concurrent.BlockingQueue;

public class GuiGetMsg implements Runnable{
    public GuiGetMsg(BlockingQueue<Package> fromCenter) {
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
