package scrabble.server.controllers.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import scrabble.protocols.Pack;
import scrabble.server.controllers.net.blockingqueue.NetGetMsg;
import scrabble.server.controllers.net.blockingqueue.NetPutMsg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.*;

public class Net implements Runnable{
    private String tag = "Net";
    private static Logger logger = Logger.getLogger(Net.class);
    private final BlockingQueue<Pack> fromCenter;
    private final BlockingQueue<Pack> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public Net(BlockingQueue fromNet, BlockingQueue toNet) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
    }

    public Hashtable getClientDataHsh() {
        return clientDataHsh;
    }

    public Hashtable getClientNameHash() {
        return clientNameHash;
    }

    private Hashtable clientDataHsh = new Hashtable(50);
    private Hashtable clientNameHash = new Hashtable(50);
    private ServerSocket server;

    private volatile static Net net;
    private Net(){
        fromCenter = new LinkedBlockingQueue<>();
        toCenter = new LinkedBlockingQueue<>();
    }
    public static Net getInstance(){
        if (net == null){
            synchronized (Net.class){
                if (net == null){
                    net = new Net();
                }
            }
        }
        return net;
    }

    public static Net getInstance (BlockingQueue fromNet, BlockingQueue toNet){
        if (net == null){
            synchronized (Net.class){
                if (net == null){
                    net = new Net(fromNet,toNet);
                }
            }
        }
        return net;
    }

    private void initialServer(int port, BlockingQueue toNetPutMsg){
        Socket client;
        int clientNumber = 1;
        try {
            server = new ServerSocket(port);
            while (flag){
                client = server.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(client
                            .getOutputStream());
                clientDataHsh.put(client,dataOutputStream);
                clientNameHash.put(clientNumber++,client);
                pool.execute(new NetThread(client,clientDataHsh,clientNameHash,toNetPutMsg,clientNumber-1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        flag = false;
    }

    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("Net-pool-%d").build();
        pool = new ThreadPoolExecutor(10,10,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        BlockingQueue<Pack> toNetPutMsg = new LinkedBlockingQueue<>();
        pool.execute(new NetGetMsg(fromCenter,clientNameHash));
        pool.execute(new NetPutMsg(toCenter,toNetPutMsg));
        initialServer(6666,toNetPutMsg);

    }
}
