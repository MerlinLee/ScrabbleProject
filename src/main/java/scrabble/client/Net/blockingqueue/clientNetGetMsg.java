package scrabble.client.Net.blockingqueue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.client.Net.clientNetSendMsg;
import scrabble.protocols.Pack;

import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.*;

public class clientNetGetMsg implements Runnable {
    private final Socket socket;
    private Hashtable clientName;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    private final BlockingQueue<String> fromCenter;


    public clientNetGetMsg(BlockingQueue<String> fromCenter, Socket socket) {
        this.fromCenter = fromCenter;
        this.socket = socket;

        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("NetGetMsg-pool-%d").build();
        pool = new ThreadPoolExecutor(3,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void run() {
        while (flag){
            try {
                String message = fromCenter.take();
                pool.execute(new clientNetSendMsg(message,socket));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown(){
        flag = false;
    }

}
