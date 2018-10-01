package scrabble.server.controllers.gameEngine.blockingqueque;

import scrabble.protocols.Pack;
import scrabble.server.controllers.gameEngine.GameProcess;

import java.util.concurrent.BlockingQueue;

public class EngineGetMsg implements Runnable {
    public EngineGetMsg(BlockingQueue<Pack> fromCenter) {
        this.fromCenter = fromCenter;
        GameProcess.getInstance().addBlockingQueue(fromCenter);
    }
    private BlockingQueue<Pack> fromCenter;


    @Override
    public void run() {

        while (true){
            Pack temp;
            try {
                temp = fromCenter.take();
//                GameProcess.getInstance().addData(temp.getUserId(), temp.getMsg());
                GameProcess.getInstance().switchProtocols(temp.getUserId(), temp.getMsg());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
