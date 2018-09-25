package scrabble.server.controllers.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

public class NetThread implements Runnable {
    private Socket client;
    private Hashtable clientDataHash;
    private Hashtable clientNameHash;
    private boolean isClientClosed = false;

    public NetThread(Socket client, Hashtable clientDataHash, Hashtable clientNameHash) {
        this.client = client;
        this.clientDataHash = clientDataHash;
        this.clientNameHash = clientNameHash;
    }

    @Override
    public void run() {
        DataInputStream inputStream;
        synchronized (clientDataHash){
            System.out.println("Current users number:"+clientDataHash.size());
        }
        try {
            inputStream = new DataInputStream(client.getInputStream());
            while (true){
                String message = inputStream.readUTF();
            }
            
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (!isClientClosed){
                closeClient();
            }
        }
    }
    private void closeClient(){}

}
