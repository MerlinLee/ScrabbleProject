package scrabble.server.controllers.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Net {
    private boolean flag = true;
    private final BlockingQueue<String> fromCenter;
    private final BlockingQueue<String> toCenter;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    private final int portNum = 6666;
    private ServerSocket serverSocket;
    public Net(BlockingQueue fromNet, BlockingQueue toNet) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
    }

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

    public void messageToCenter(String message){
        try {
            toCenter.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    private void initialServerNetwork(){
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("client-pool-%d").build();
        pool = new ThreadPoolExecutor(3,10,0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        bindServerSocket(this.portNum);
    }

    private void bindServerSocket(int serverPort){
        //count is for allocating client identity number.
        int count=0;
        try {
            serverSocket = new ServerSocket(serverPort);
            Socket client;
            while (flag){
                client = serverSocket.accept();
                pool.execute(new ClientService(client,count));
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        this.flag = false;
    }
}
