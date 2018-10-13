package scrabble.client.Net.blockingqueue;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;
import scrabble.protocols.ErrorProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.serverResponse.NonGamingResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class clientNetThread implements Runnable {
    private Socket server;
    private Hashtable clientDataHash;
    private Hashtable clientNameHash;
    private boolean isClientClosed = false;
    private final BlockingQueue<String> toNetPutMsg;
    private boolean flag = true;

    public clientNetThread(Socket server, BlockingQueue toNetPutMsg) {
        this.server = server;
        this.toNetPutMsg = toNetPutMsg;
    }

    @Override
    public void run() {

        BufferedReader inputStream;
        try {

            inputStream = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while (flag){
                if(server.isClosed()==false&&server.isConnected()==true){
                    String message = inputStream.readLine();
                    if(message==null){
                        flag=false;
                    }else {
                        toNetPutMsg.put(bouncyCastleBase64(message));
                    }
//                Pack msg = new Pack(-1,message);
                    //System.out.println(message);

                }else {
                    closeClient();
                }
            }

        }catch (Exception e){
            System.out.println("Connection Closed!");
        }finally {
            if (!isClientClosed){
                closeClient();
            }
        }
    }
    private void closeClient(){
        try {
            toNetPutMsg.put(JSON.toJSONString(new ErrorProtocol("The server has been shutdown", "other")));
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System shutdown!");
    }

    private  String bouncyCastleBase64 (String cipher) {

        byte[] decodeBytes = Base64.getDecoder().decode(cipher);
        return new String(decodeBytes);
    }
}
