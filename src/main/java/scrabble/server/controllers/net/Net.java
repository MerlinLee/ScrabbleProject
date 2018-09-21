package scrabble.server.controllers.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Net {

    private final BlockingQueue<String> fromCenter;
    private final BlockingQueue<String> toCenter;

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
}
