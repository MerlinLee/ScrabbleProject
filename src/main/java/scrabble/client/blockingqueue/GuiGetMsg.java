package scrabble.client.blockingqueue;

import scrabble.client.gui.GuiController;
import scrabble.client.gui.GuiListener;
import scrabble.protocols.Pack;
import scrabble.server.controllers.gameEngine.GameProcess;

import java.util.concurrent.BlockingQueue;

public class GuiGetMsg implements Runnable{
    public GuiGetMsg(BlockingQueue<String> fromCenter) {
    this.fromCenter = fromCenter;
    GuiListener.get().addBlockingQueue(fromCenter);
}
    private BlockingQueue<String> fromCenter;


    @Override
    public void run() {

        while (true){
            String temp;
            try {
                temp = fromCenter.take();
//                GuiController.get().receiveMsgFromCenter(temp);
                synchronized (GuiListener.get()){
                    GuiListener.get().addMessage(temp);
                }
                System.out.println(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
