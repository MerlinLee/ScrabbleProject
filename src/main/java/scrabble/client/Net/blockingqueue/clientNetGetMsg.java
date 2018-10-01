package scrabble.client.Net.blockingqueue;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.client.Net.clientNetSendMsg;

import java.util.Hashtable;
import java.util.concurrent.*;

public class clientNetGetMsg implements Runnable {
    private Hashtable clientName;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public clientNetGetMsg(BlockingQueue<Pack> fromCenter) {
        this.fromCenter = fromCenter;

        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("NetGetMsg-pool-%d").build();
        pool = new ThreadPoolExecutor(3,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
    }

    private final BlockingQueue<Pack> fromCenter;
    @Override
    public void run() {
        while (flag){
            try {
                Pack message = fromCenter.take();
                pool.execute(new clientNetSendMsg(message));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown(){
        flag = false;
    }

}
