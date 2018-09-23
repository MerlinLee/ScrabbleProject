package scrabble.server.controllers.net;

import java.io.*;
import java.net.Socket;

public class ClientService implements Runnable{
    private Socket client;
    private int clientID
;
    public ClientService(Socket client,int clientID) {
        this.client = client;
        this.clientID = clientID;
    }

    @Override
    public void run() {
        NetClientSocketDB.getInstance().setSocketDB(client,clientID);
        Net.getInstance().messageToCenter(getMessage(client));
    }

    private static String getMessage(Socket client){
        //operation here
        String message="";
        try (InputStream is=client.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);
             ){

            message = br.readLine();

        }catch (IOException e){
            e.printStackTrace();
        }
        return message;
    }
}
