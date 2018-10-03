package scrabble.server.controllers.net;

import scrabble.protocols.Pack;

import java.io.*;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class NetSendMsg implements Runnable {
    private Hashtable clientNameTable;
    private Socket client;
    private Pack message;
    public NetSendMsg(Pack message, Hashtable clientNameTable) {
        this.message=message;
        this.clientNameTable=clientNameTable;
    }
    @Override
    public void run() {

            if(message.getRecipient()==null){
                if(message.getUserId()==0){
                    sendBroadcastMsg(message.getMsg());
                }else {
                    sendToPeer(message.getMsg(),message.getUserId());
                }
            }else {
                int peerNum = message.getRecipient().length;
                for(int i=0;i<peerNum;i++){
                    sendToPeer(message.getMsg(),message.getRecipient()[i]);
                }
            }

    }

    private void sendBroadcastMsg(String msg){
        synchronized (clientNameTable){
            for(Enumeration enu=clientNameTable.elements();enu.hasMoreElements();){
                client = (Socket)enu.nextElement();
                sendMsgOperation(msg);
            }
        }
    }

    private void sendToPeer(String msg, int clientId){
        client = (Socket)clientNameTable.get(clientId);
        sendMsgOperation(msg);
    }

    private void sendMsgOperation(String msg){
        try {
            PrintWriter printWriter = new PrintWriter(new DataOutputStream(client.getOutputStream()));
            printWriter.println(msg);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
