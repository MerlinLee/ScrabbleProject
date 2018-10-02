package scrabble.server.controllers.net;

import com.alibaba.fastjson.JSON;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.Pack;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

public class NetThread implements Runnable {
    private Socket client;
    private Hashtable clientDataHash;
    private Hashtable clientNameHash;
    private boolean isClientClosed = false;
    private final BlockingQueue<Pack> toNetPutMsg;
    private int clientID;

    public NetThread(Socket client, Hashtable clientDataHash, Hashtable clientNameHash,BlockingQueue toNetPutMsg,int clientID) {
        this.client = client;
        this.clientDataHash = clientDataHash;
        this.clientNameHash = clientNameHash;
        this.toNetPutMsg = toNetPutMsg;
        this.clientID = clientID;
    }

    @Override
    public void run() {
//        DataInputStream inputStream;
        BufferedReader inputStream;
        synchronized (clientDataHash){
            System.out.println("Current users number:"+clientDataHash.size());
        }
        try {
//            inputStream = new DataInputStream(client.getInputStream());
            inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true){
                String message = inputStream.readLine();
                toNetPutMsg.put(new Pack(clientID,message));
                if(client.isConnected()){
                    break;
                }
            }

        }catch (Exception e){
            closeClient();
        }
    }
    private void closeClient(){
        try {
            System.out.println("client "+clientID+" is closed!");
            toNetPutMsg.put(new Pack(clientID, JSON.toJSONString(new GamingOperationProtocol("disconnect"))));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
