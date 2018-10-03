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
    private boolean flag = true;
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
            while (flag){

                if(client.isClosed()==false&&client.isConnected()==true){
                    String message = inputStream.readLine();
                    toNetPutMsg.put(new Pack(clientID,message));
                }else {
                    flag=false;
                    client.close();
                    break;
                }
                if(client.isConnected()==false||client.isClosed()==true){
                    flag=false;
                    client.close();
                    break;
                }
            }

        }catch (Exception e){
            closeClient();
        }
    }
    private void closeClient(){
        try {
            client.close();
            System.out.println("client "+clientID+" is closed!");
            Net.getInstance().getClientDataHsh().remove(client);
            Net.getInstance().getClientNameHash().remove(clientID);
            toNetPutMsg.put(new Pack(clientID, JSON.toJSONString(new GamingOperationProtocol("disconnect"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
