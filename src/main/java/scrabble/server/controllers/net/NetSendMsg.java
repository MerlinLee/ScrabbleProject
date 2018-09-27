package scrabble.server.controllers.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class NetSendMsg implements Runnable {
    private Hashtable clientNameTable;
    private Socket client;
    private String message;
    public NetSendMsg(String message,Hashtable clientNameTable) {
        this.message=message;
        this.clientNameTable=clientNameTable;
    }
    @Override
    public void run() {

    }

    private void sendBroadcastMsg(String msg){
        synchronized (clientNameTable){
            for(Enumeration enu=clientNameTable.elements();enu.hasMoreElements();){
                client = (Socket)enu.nextElement();
                sendMsgOperation();
            }
        }
    }

    private void sendToPeer(String msg, int clientId){
        client = (Socket)clientNameTable.get(clientId);
        sendMsgOperation();
    }

    private void sendMsgOperation(){
        try {
            PrintWriter printWriter = new PrintWriter(new DataOutputStream(client.getOutputStream()));
            printWriter.println("hello");
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
