package scrabble.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.client.blockingqueue.GuiGetMsg;
import scrabble.client.blockingqueue.GuiPutMsg;

import java.util.concurrent.*;

public class Gui implements Runnable{
    private BlockingQueue<Package> fromCenter;
    private BlockingQueue<Package> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    public Gui(BlockingQueue<Package> toGui, BlockingQueue<Package> fromGui) {
        this.fromCenter = toGui;
        this.toCenter = fromGui;
    }

    private volatile static Gui gui;
    public Gui(){}
    public static Gui getInstance(){
        if (gui == null ){
            synchronized (Gui.class){
                if (gui == null){
                    gui = new Gui();
                }
            }
        }
        return gui;
    }

    public static Gui getInstance(BlockingQueue<Package> toGui, BlockingQueue<Package> fromGui){
        if (gui == null ){
            synchronized (Gui.class){
                if (gui == null){
                    gui = new Gui(toGui, fromGui);
                }
            }
        }
        return gui;
    }

    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(2,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new GuiGetMsg(fromCenter));
        pool.execute(new GuiPutMsg(toCenter));
    }

    public void shutdown(){
        flag = false;
    }
}
