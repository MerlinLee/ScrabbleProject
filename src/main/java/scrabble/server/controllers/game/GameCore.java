package scrabble.server.controllers.game;

import com.alibaba.fastjson.JSON;
import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.BlockingQueue;

public class GameCore {
    private final BlockingQueue<String> blockingQueue;
    private volatile boolean flag;
    public GameCore(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        flag = false;
    }

    //test get message from net layer
    public void getMessageFromNet(){
        String message = "";
        try {
            String packet = blockingQueue.take();
            message = packet;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!message.equals("")){
            ScrabbleProtocol scrabbleProtocol = JSON.parseObject(message,ScrabbleProtocol.class);
            System.out.println(scrabbleProtocol.getTAG());
        }else {
            System.out.println("error");
        }
    }
}
