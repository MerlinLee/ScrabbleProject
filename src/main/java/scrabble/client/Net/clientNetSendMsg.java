package scrabble.client.Net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class clientNetSendMsg implements Runnable{

    private Socket client;
    private String message;
    public clientNetSendMsg(String message) {
        this.message=message;

    }
    @Override
    public void run() {

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
