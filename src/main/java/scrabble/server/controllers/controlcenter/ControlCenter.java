package scrabble.server.controllers.controlcenter;

import com.alibaba.fastjson.JSON;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.server.controllers.gameEngine.GameEngine;
import scrabble.server.controllers.net.Net;
import scrabble.server.controllers.net.NetCore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ControlCenter implements Runnable{
    private final BlockingQueue<String> fromNet;
    private final BlockingQueue<ScrabbleProtocol> toEngine;
    private final BlockingQueue<ScrabbleProtocol> fromEngine;
    private final BlockingQueue<String> toNet;
    private GameEngine gameEngine;
    private Net net;

    public ControlCenter() {
        this.fromNet = new LinkedBlockingQueue<>();
        toEngine = new LinkedBlockingQueue<>();
        fromEngine = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
        initialServer();
    }

    public void initialServer(){
        net = Net.getInstance(fromNet,toNet);
    }
    @Override
    public void run() {
        getMessage();
    }

    public void getMessage(){
        String message=null;
        try {
            message = fromNet.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!message.equals("")){
            ScrabbleProtocol scrabbleProtocol = toObject(message);
        }
    }

    private ScrabbleProtocol toObject(String message){
        ScrabbleProtocol scrabbleProtocol =  JSON.parseObject(message,ScrabbleProtocol.class);
        switch (scrabbleProtocol.getTAG()){
                case "NonGamingProtocol":
                    return JSON.parseObject(message, NonGamingProtocol.class);
                case "GamingOperationProtocol":
                    return JSON.parseObject(message, GamingOperationProtocol.class);
                default:
                    break;
        }
        return null;
    }
}
