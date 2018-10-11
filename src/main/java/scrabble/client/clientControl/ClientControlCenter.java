package scrabble.client.clientControl;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import scrabble.client.Gui;
import scrabble.client.Net.blockingqueue.ClientNet;
import scrabble.client.blockingqueue.GuiGetMsg;
import scrabble.client.blockingqueue.GuiPutMsg;
import scrabble.client.clientControl.blockingqueue.ClientCenterGetMsg;
import scrabble.client.clientControl.blockingqueue.ClientCenterPutMsg;
import scrabble.client.gui.LoginWindow;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;

import java.util.concurrent.*;

public class ClientControlCenter implements Runnable{
    private String tag = "clientControl";
    private static Logger logger = Logger.getLogger(ClientControlCenter.class);
    private final BlockingQueue<String> fromNet;
    private final BlockingQueue<String> toGui;
    private final BlockingQueue<String> fromGui;
    private final BlockingQueue<String> toNet;
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
                .setNameFormat("Client-ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(10,50,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
//        pool.execute(ClientNet.getInstance(fromNet,toNet));
//        pool.execute(Gui.getInstance(toGui,fromGui));
        logger.info(tag+" Initial Server Competed");
    }

    public void openNet(String ipAddr, int portNum,String username){
        try {
            pool.execute(ClientNet.getInstance(fromNet,toNet,ipAddr,portNum,username));
        }catch (Exception e){
            pool.execute(LoginWindow.get());
        }
    }

    @Override
    public void run() {
        initialClient();
        pool.execute(new ClientCenterGetMsg(fromNet,toGui,fromGui,toNet));
        pool.execute(new ClientCenterPutMsg(fromNet,toGui,fromGui,toNet));
        LoginWindow.get().setCenter(this);
        pool.execute(LoginWindow.get());
        pool.execute(new GuiGetMsg(toGui));
        GuiPutMsg.getInstance(fromGui);
        //开启gui
//        loginWindow = LoginWindow.get();
//        loginWindow.setClient(this);
//        Thread loginThread = new Thread(loginWindow);
//        loginThread.start();
    }


    public void shutdown(){
        flag = false;
    }

}
