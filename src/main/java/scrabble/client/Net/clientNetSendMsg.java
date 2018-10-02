package scrabble.client.Net;

import scrabble.protocols.Pack;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class clientNetSendMsg implements Runnable{

    private Socket client;
    private Pack message;
    private Pack msg;
    private Socket socket;

    public clientNetSendMsg(Pack message,Socket socket) {
        this.message=message;
        this.socket=socket;
    }
    @Override
    public void run() {
        sendToPeer(message);
    }
    private void sendToPeer(Pack msg){

        sendMsgOperation(msg,socket);
    }

    private void sendMsgOperation(Pack msg, Socket socket){
        try {

            PrintWriter out=new PrintWriter(socket.getOutputStream());
            out.println(msg.getMsg());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
