package scrabble.client;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import scrabble.client.blockingqueue.GuiGetMsg;
import scrabble.client.blockingqueue.GuiPutMsg;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;

import java.util.Scanner;
import java.util.concurrent.*;

public class Gui implements Runnable {
    private BlockingQueue<String> fromCenter;
    private BlockingQueue<String> toCenter;
    private boolean flag = true;
    private ThreadFactory threadForSocket;
    private ExecutorService pool;

    public Gui(BlockingQueue<String> toGui, BlockingQueue<String> fromGui) {
        this.fromCenter = toGui;
        this.toCenter = fromGui;
    }

    private volatile static Gui gui;

    public Gui() {
    }

    public static Gui getInstance() {
        if (gui == null) {
            synchronized (Gui.class) {
                if (gui == null) {
                    gui = new Gui();
                }
            }
        }
        return gui;
    }

    public static Gui getInstance(BlockingQueue<String> toGui, BlockingQueue<String> fromGui) {
        if (gui == null) {
            synchronized (Gui.class) {
                if (gui == null) {
                    gui = new Gui(toGui, fromGui);
                }
            }
        }
        return gui;
    }

    public void sendMsg() {
        String msg = null;
        Pack pack;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Please select protocol: GamingOperationProtocol   NonGamingProtocol");
            switch (scanner.nextLine()) {
                case "GamingOperationProtocol":
                    msg = scanner.nextLine();

                    try {
                        toCenter.put(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String trash = scanner.nextLine();
                   // getMsg();
                    break;
                case "NonGamingProtocol":
                    msg = scanner.nextLine();
                    String[] temp = msg.split(",");
                    String[] user = null;
                    NonGamingProtocol ngp = new NonGamingProtocol(temp[0], user);

                    switch (temp[0]) {
                        case "login":
                            user = new String[]{temp[1]};
                            ngp.setUserList(user);
                            break;
                        case "start":
                            break;
                        case "logout":
                            break;
                        case "invite":
                            user = new String[temp.length-1];
                            for (int i=0; i< user.length;i++) {
                                user[i] = temp[i+1];
                            }
                            ngp.setUserList(user);
                            break;
                        case "inviteResponse":
                            ngp.setHostID(1); //previous Pack's hostID
                            if (temp[1].equals("yes")){
                            ngp.setInviteAccepted(true);}
                            else{
                                ngp.setInviteAccepted(false);
                            }
                            break;
                        default:
                            break;
                    }

                    try {
                        toCenter.put(JSON.toJSONString(ngp));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //getMsg(); // 阻塞， 分开
                    break;
                default:
                    break;
            }
        }
    }

    private void getMsg() {
        String pack = null;

        try {
            pack = fromCenter.take();
//            System.out.println(pack.getMsg());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        threadForSocket = new ThreadFactoryBuilder()
                .setNameFormat("ControlCenter-pool-%d").build();
        pool = new ThreadPoolExecutor(2, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadForSocket, new ThreadPoolExecutor.AbortPolicy());
        pool.execute(new GuiGetMsg(fromCenter));
        pool.execute(new GuiPutMsg(toCenter));
        sendMsg();

    }

    public void shutdown() {
        flag = false;
    }
}
