package scrabble.client.clientControl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import scrabble.client.Gui;
import scrabble.client.Net.blockingqueue.ClientNet;
import scrabble.client.clientControl.blockingqueue.ClientCenterGetMsg;
import scrabble.client.clientControl.blockingqueue.ClientCenterPutMsg;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.*;

public class ClientControlCenter implements Runnable{
    private String tag = "clientControl";
    private static Logger logger = Logger.getLogger(ClientControlCenter.class);
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toGui;
    private final BlockingQueue<Pack> fromGui;
    private final BlockingQueue<Pack> toNet;
    private Gui gui;
    private ClientNet net;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public ClientControlCenter() {
        this.fromNet = new LinkedBlockingQueue<>();
        toGui = new LinkedBlockingQueue<>();
        fromGui = new LinkedBlockingQueue<>();
        toNet = new LinkedBlockingQueue<>();
//        initialClient();
        logger.info(tag+" Initial clientControlCenter Complete!");
    }
    public void initialClient(){
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(5,10,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(ClientNet.getInstance(fromNet,toNet));
        pool.execute(Gui.getInstance(toGui,fromGui));
        logger.info(tag+" Initial Server Competed");
    }


    @Override
    public void run() {
        pool.execute(new ClientCenterGetMsg(fromNet,toGui,fromGui,toNet));
        pool.execute(new ClientCenterPutMsg(fromNet,toGui,fromGui,toNet));
        initialClient();

        //开启gui

    }


    public void shutdown(){
        flag = false;
    }

}
