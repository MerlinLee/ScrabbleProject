package scrabble.client.blockingqueue;

import scrabble.protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class GuiGetMsg implements Runnable{
    public GuiGetMsg(BlockingQueue<String> fromCenter) {
        this.fromCenter = fromCenter;
    }

    private BlockingQueue<String> fromCenter;
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
