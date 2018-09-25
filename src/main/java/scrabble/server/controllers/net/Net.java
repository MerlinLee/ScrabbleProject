package scrabble.server.controllers.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Net {

    private final BlockingQueue<String> fromCenter;
    private final BlockingQueue<String> toCenter;
    private boolean flag = true;

    public Net(BlockingQueue fromNet, BlockingQueue toNet) {
        this.toCenter = fromNet;
        this.fromCenter = toNet;
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

    private void initialServer(int port){
        Socket client;
        int clientNumber = 1;
        try {
            server = new ServerSocket(port);
            while (flag){
                client = server.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(client
                            .getOutputStream());
                clientDataHsh.put(client,dataOutputStream);
                clientNameHash.put(client,clientNumber++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        flag = false;
    }
}
