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

    //test
    public static void main(String[] args){
        GameNet gameNet = new GameNet();
        new Thread().start();
        new Thread().start();
    }
}
