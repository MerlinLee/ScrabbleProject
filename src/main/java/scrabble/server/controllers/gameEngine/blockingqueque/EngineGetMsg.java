package scrabble.server.controllers.gameEngine.blockingqueque;

import scrabble.protocols.Package;
import scrabble.server.controllers.gameEngine.GameProcess;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class EngineGetMsg implements Runnable {
    public EngineGetMsg(BlockingQueue<Package> fromCenter) {
        this.fromCenter = fromCenter;
    }
    private BlockingQueue<Package> fromCenter;


    @Override
    public void run() {
        while (true){
            Package temp;
            try {
                temp = fromCenter.take();
                GameProcess.getInstance().addData(temp.getUserId(), temp.getMsg());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
