package scrabble.server.controllers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
* Merlin Lee
* 21/09/2018
* BlockingQueue
* */
//Singleton Design Pattern
public class GameNet {
    private BlockingQueue<String> blockingQueue;

    public GameNet() {
        this.blockingQueue = new LinkedBlockingQueue<String>(10);
    }

    public BlockingQueue<String> getBlockingQueue() {
        return blockingQueue;
    }
}
