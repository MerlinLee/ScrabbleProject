package scrabble.server.controllers.net;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class NetClientSocketDB {
    private volatile static NetClientSocketDB netClientSocketDB;
    private ConcurrentHashMap<Integer, Socket> socketDB;

    public NetClientSocketDB() {
        socketDB = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, Socket> getSocketDB() {
        return socketDB;
    }

    public void setSocketDB(Socket client,int clientID) {
        this.socketDB.put(clientID,client);
    }

    public static NetClientSocketDB getInstance(){
        if(netClientSocketDB == null){
            synchronized (NetClientSocketDB.class){
                if(netClientSocketDB ==null){
                    netClientSocketDB = new NetClientSocketDB();
                }
            }
        }
        return netClientSocketDB;
    }
}
