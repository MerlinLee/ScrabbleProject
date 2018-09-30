package scrabble.client.clientControl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import scrabble.client.Net.clientNet;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Package;
import scrabble.protocols.ScrabbleProtocol;

import java.util.Scanner;
import java.util.concurrent.*;

public class clientControlCenter implements Runnable{
    private String tag = "clientControl";
    private static Logger logger = Logger.getLogger(clientControlCenter.class);
    private final BlockingQueue<String> fromNet;
    private final BlockingQueue<Package> toGui;
    private final BlockingQueue<Package> fromGui;
    private final BlockingQueue<String> toNet;
    private GameEngine gameEngine;
    private clientNet net;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public clientControlCenter() {
        this.fromNet = new LinkedBlockingQueue<>();
        toGui = new LinkedBlockingQueue<>();
        fromGui = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
        initialClient();
        logger.info(tag+" Initial clientControlCenter Complete!");
    }
    public void initialClient(){
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(5,10,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(clientNet.getInstance(fromNet,toNet));
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
        String message=null;
        try {
            message = fromNet.take();
            sendMsgToNet("hello");
            logger.info(tag+" get message from queue!");
        } catch (InterruptedException e) {
            logger.error(tag+e);
        }
        if(!message.equals("")){
            // ScrabbleProtocol scrabbleProtocol = toObject(message);
            System.out.println(message);
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
    public void shutdown(){
        flag = false;
    }

    private void sendMsgToNet(String msg){
        try {
            toNet.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
