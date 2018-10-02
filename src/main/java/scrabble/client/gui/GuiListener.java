package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.protocols.serverResponse.InviteACK;
import scrabble.protocols.serverResponse.NonGamingResponse;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class GuiListener {

    private static GuiListener instance = null;
    private BlockingQueue<String> queue;

    private GuiListener() {

    }

    public static synchronized GuiListener get() {
        if (instance == null) {
            instance = new GuiListener();
        }
        return instance;
    }
    public void addBlockingQueue(BlockingQueue queue){
        this.queue = queue;
    }

    public void addMessage(String str) {
        //System.out.println(str);
        ScrabbleProtocol scrabbleProtocol = JSON.parseObject(str, ScrabbleProtocol.class);
        String tag = scrabbleProtocol.getTAG();
        switch (tag) {
            case "NonGamingResponse":
                processNonGamingResonse(str);
                break;
            case "InviteACK":
                InviteACK inviteRespond = JSON.parseObject(str, InviteACK.class);
                int iid = inviteRespond.getId();
                boolean ac = inviteRespond.isAccept();
                GuiController.get().showInviteRespond(iid, ac);
                break;
            case "GamingSync":
                break;
            case "VoteRequest":
                break;
        }
    }

    private void processNonGamingResonse(String str) {
        NonGamingResponse respond = JSON.parseObject(str, NonGamingResponse.class);
        String command;
        command = respond.getCommand();
        switch (command) {
            case "userUpdate":
                Users[] users = respond.getUsersList();
                GuiController.get().updateUserList(users);
                break;
            case "invite":
                int inviterId = respond.getUsersList()[0].getUserID();
                String inviterName = respond.getUsersList()[0].getUserName();
                GuiController.get().showInviteMessage(inviterId, inviterName);
                break;
        }
        Users[] users = respond.getUsersList();
        //String status = respond.getStatus();
        GuiController.get().showLoginRespond(users, "Free");
    }
}
