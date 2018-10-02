package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.protocols.serverResponse.InviteACK;
import scrabble.protocols.serverResponse.NonGamingResponse;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.InputStreamReader;

public class ClientListener extends Thread {

    private Socket socket;
    private ClientController client;

    private static ClientListener instance = null;

    private ClientListener(Socket socket, ClientController client) {
        this.socket = socket;
        this.client = client;
    }

    public static synchronized ClientListener get(Socket socket, ClientController client) {
        if (instance == null) {
            instance = new ClientListener(socket, client);
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(input);
            while (true) {
                String str = br.readLine();
                System.out.println(str);
                ScrabbleProtocol scrabbleProtocol = JSON.parseObject(str, ScrabbleProtocol.class);
                ///////////////////////////////////////////
                String tag = scrabbleProtocol.getTAG();
                switch (tag) {
                    case "NonGamingResponse":
                        NonGamingResponse respond = JSON.parseObject(str, NonGamingResponse.class);
                        Users[] users = respond.getUsersList();
                        //String status = respond.getStatus();
                        client.showLoginRespond(users, "Free");
                        break;
                    case "InviteResponse":
                        InviteACK inviteRespond = JSON.parseObject(str, InviteACK.class);
                        int iid = inviteRespond.getId();
                        boolean ac = inviteRespond.isAccept();
                        client.showInviteRespond(iid, ac);
                        break;
                }

            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
