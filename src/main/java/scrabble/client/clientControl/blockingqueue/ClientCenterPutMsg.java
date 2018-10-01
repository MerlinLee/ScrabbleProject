/*
 * Copyright (c) 2018. Mingfeng Li The University of Melbourne
 */

package scrabble.client.clientControl.blockingqueue;

import scrabble.protocols.Pack;

import java.util.concurrent.BlockingQueue;

public class ClientCenterPutMsg implements Runnable {
    private final BlockingQueue<Pack> fromNet;
    private final BlockingQueue<Pack> toGui;
    private final BlockingQueue<Pack> fromGui;
    private final BlockingQueue<Pack> toNet;

    public ClientCenterPutMsg(BlockingQueue<Pack> fromNet, BlockingQueue<Pack> toGui, BlockingQueue<Pack> fromGui, BlockingQueue<Pack> toNet) {
        this.fromNet = fromNet;
        this.toGui = toGui;
        this.fromGui = fromGui;
        this.toNet = toNet;
    }

    @Override
    public void run() {
        try {
            toNet.put(fromGui.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
