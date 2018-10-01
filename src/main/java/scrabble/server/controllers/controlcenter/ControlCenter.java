package scrabble.server.controllers.controlcenter;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.server.controllers.gameEngine.GameEngine;
import scrabble.server.controllers.net.Net;

import java.util.Scanner;
import java.util.concurrent.*;

public class ControlCenter implements Runnable{
    private String tag = "ControlCenter";
    private static Logger logger = Logger.getLogger(ControlCenter.class);
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toEngine;
    private final BlockingQueue<Pack> fromEngine;
    private final BlockingQueue<Pack> toNet;
    private GameEngine gameEngine;
    private Net net;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public ControlCenter() {
        this.fromNet = new LinkedBlockingQueue<>();
        toEngine = new LinkedBlockingQueue<>();
        fromEngine = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
        initialServer();
        logger.info(tag+" Initial ControlCenter Complete!");
    }

    public void initialServer(){
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(5,10,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(Net.getInstance(fromNet,toNet));
        pool.execute(GameEngine.getInstance(toEngine,fromEngine));
        logger.info(tag+" Initial Server Competed");
    }
    @Override
    public void run() {
        Scanner read = new Scanner(System.in);
        while (true){
            getMessage();
        }
    }

    public void getMessage(){
        Pack message=null;
        try {
            message = fromNet.take();
            sendMsgToNet(message);
            logger.info(tag+" get message from queue!");
        } catch (InterruptedException e) {
            logger.error(tag+e);
        }
        if(!message.equals("")){
           // ScrabbleProtocol scrabbleProtocol = toObject(message);
            System.out.println(message);
        }
    }


    public void shutdown(){
        flag = false;
    }

    private void sendMsgToNet(Pack msg){
        try {
            toNet.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
