package scrabble.client;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.client.blockingqueue.GuiGetMsg;
import scrabble.client.blockingqueue.GuiPutMsg;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;

import java.util.Scanner;
import java.util.concurrent.*;

public class Gui implements Runnable{
    private BlockingQueue<Pack> fromCenter;
    private BlockingQueue<Pack> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;
    public Gui(BlockingQueue<Pack> toGui, BlockingQueue<Pack> fromGui) {
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

    public static Gui getInstance(BlockingQueue<Pack> toGui, BlockingQueue<Pack> fromGui){
        if (gui == null ){
            synchronized (Gui.class){
                if (gui == null){
                    gui = new Gui(toGui, fromGui);
                }
            }
        }
        return gui;
    }

    public void test() {
        String msg = null;
        Pack pack;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Please select protocol: GamingOperationProtocol   NonGamingProtocol");
            switch (scanner.nextLine()) {
                case "GamingOperationProtocol":
                    msg = scanner.nextLine();
                    pack = new Pack(-1, msg);
                    try {
                        toCenter.put(pack);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "NonGamingProtocol":
                    msg = scanner.nextLine();
                    String[] user = new String[1];
                    user[0]=msg;
                    pack = new Pack(-1, JSON.toJSONString(new NonGamingProtocol("login",user)));
                    try {
                        toCenter.put(pack);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(2,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),threadForSocket,new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new GuiGetMsg(fromCenter));
        pool.execute(new GuiPutMsg(toCenter));
        test();
    }

    public void shutdown(){
        flag = false;
    }
}
