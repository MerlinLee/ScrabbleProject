package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.ScrabbleProtocol;

import java.io.PrintWriter;
import java.net.Socket;

public class ClientSender extends Thread {

    private Socket socket;
    private PrintWriter output;

    private static ClientSender instance = null;

    private ClientSender(Socket socket) {
        this.socket = socket;
    }

    public static synchronized ClientSender get(Socket socket) {
        if (instance == null) {
            instance = new ClientSender(socket);
        }
        return instance;
    }

    public void sendToServer(ScrabbleProtocol scrabbleProtocol) {
        try {
            String json = JSON.toJSONString(scrabbleProtocol);
            //System.out.println("Trans before send");
            NonGamingProtocol protocol = (NonGamingProtocol) scrabbleProtocol;
            System.out.println(JSON.toJSONString(protocol));
            output.println(json);
            output.flush();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
