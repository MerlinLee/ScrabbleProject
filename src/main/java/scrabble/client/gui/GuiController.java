package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;
import scrabble.client.blockingqueue.GuiPutMsg;
import scrabble.protocols.GamingProtocol.BrickPlacing;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.protocols.serverResponse.InviteACK;
import scrabble.protocols.serverResponse.NonGamingResponse;

import java.net.Socket;
import java.net.UnknownHostException;

public class GuiController {

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private String id;

    private GameWindow gameWindow;
    private LoginWindow loginWindow;
    private GameLobbyWindow gameLobbyWindow;

    private volatile static GuiController guiController;

    public static GuiController  get() {
        if (guiController == null) {
            synchronized (GuiController.class){
                if(guiController==null){
                    guiController=new GuiController();
                }
            }
        }
        return guiController;
    }

    /*
    ClientController() {
        loginWindow = LoginWindow.get();
        loginWindow.setClient(this);
        Thread loginThread = new Thread(loginWindow);
        loginThread.start();
    }
    */

    private void runGameLobbyWindow() {
        gameLobbyWindow = GameLobbyWindow.get();
        gameLobbyWindow.setModel();
        Thread lobbyThread = new Thread(gameLobbyWindow);
        lobbyThread.start();
    }

    private void runGameWindow() {
        gameWindow = GameWindow.get();
        Thread gameThread = new Thread(gameWindow);
        gameThread.start();
    }

    void startOneTurn() {
        gameWindow.startOneTurn();
    }

    void loginGame() {
//        String[] selfArray = new String[1];
//        selfArray[0] = username;
//        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("login", selfArray);
//        GuiSender.get().sendToCenter(nonGamingProtocol);
        runGameLobbyWindow();
    }

    void invitePlayers(String[] players) {
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteOperation", players);
//        GuiSender.get().sendToCenter(nonGamingProtocol);
        GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(nonGamingProtocol));
    }

    void startGame(String[] players) {
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("start", players);
        GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(nonGamingProtocol));
        runGameWindow();
    }

    void quitGame() {
        String[] selfArray = new String[1];
        selfArray[0] = username;
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("quit", selfArray);
//        GuiSender.get().sendToCenter(nonGamingProtocol);
        GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(nonGamingProtocol));
    }

    void logoutGame() {
        try {
            String[] selfArray = new String[1];
            selfArray[0] = username;
            NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("logout", selfArray);
//            GuiSender.get().sendToCenter(nonGamingProtocol);
            GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(nonGamingProtocol));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void sendPass(int[] lastMove, char c) {
        try {
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("vote");
            gamingProtocol.setVote(false);
            BrickPlacing brickPlacing = new BrickPlacing();
            brickPlacing.setbrick(c);
            brickPlacing.setPosition(lastMove);
            gamingProtocol.setBrickPlacing(brickPlacing);
//            GuiSender.get().sendToCenter(gamingProtocol);
            GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(gamingProtocol));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void sendVote(int[] lastMove, char c, int sx, int sy, int ex, int ey) {
        try {
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("vote");
            gamingProtocol.setVote(true);
            BrickPlacing brickPlacing = new BrickPlacing();
            brickPlacing.setbrick(c);
            brickPlacing.setPosition(lastMove);
            gamingProtocol.setBrickPlacing(brickPlacing);
            int[] startPosition = new int[2];
            startPosition[0] = sx;
            startPosition[1] = sy;
            int[] endPosition = new int[2];
            endPosition[0] = ex;
            endPosition[1] = ey;
            gamingProtocol.setStartPosition(startPosition);
            gamingProtocol.setStartPosition(endPosition);
//            GuiSender.get().sendToCenter(gamingProtocol);
            GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(gamingProtocol));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void showLoginRespond(Users[] users, String status) {
        this.id = Integer.toString(users[0].getUserID());
//        gameLobbyWindow.updateUserList(users[0].getUserID(), users[0].getUserName(), users[0].getStatus());
    }

    void showInviteRespond(int id, boolean ac) {
        if (ac) {
//            gameLobbyWindow.addToPlayerList(id);
        }
        else {
            gameLobbyWindow.refuseInvite(id);
        }
    }

    void updateUserList(Users[] userList) {
        gameLobbyWindow.updateUserList(userList);
    }

    void showInviteMessage(int inviterId, String inviterName) {
        gameLobbyWindow.showInviteMessage(inviterId, inviterName);
    }

    void sendInviteResponse(boolean ack, int inviterId) {
        String[] userList = new String[1];
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteResponse", userList);
        nonGamingProtocol.setInviteAccepted(ack);
        nonGamingProtocol.setHostID(inviterId);
//        GuiSender.get().sendToCenter(nonGamingProtocol);
        GuiPutMsg.getInstance().putMsgToCenter(JSON.toJSONString(nonGamingProtocol));
    }

    String getId() {
        return id;
    }

    public void receiveMsgFromCenter(String msg){
        ScrabbleProtocol scrabbleProtocol = JSON.parseObject(msg,ScrabbleProtocol.class);
        switch (scrabbleProtocol.getTAG()){
            case "NonGamingResponse":
                switchMethods(JSON.parseObject(msg,NonGamingResponse.class));
                break;
            case "NonGamingProtocol":
                switchMethods(JSON.parseObject(msg,NonGamingProtocol.class));
                break;
                case "InviteACK":
                    switchMethods(JSON.parseObject(msg,InviteACK.class));
                    break;
                default:
                    break;
        }
    }

    private void findMyId(Users[] usersLisr){
        for(Users users:usersLisr){
            if(users.getUserName().equals(this.username)){
                this.id=String.valueOf(users.getUserID());
            }
        }
    }

    private void switchMethods(NonGamingResponse protocol){
        if(protocol.getCommand().equals("userUpdate")){
            findMyId(protocol.getUsersList());
            GameLobbyWindow.get().updateUserList(protocol.getUsersList());
        }

    }

    private void switchMethods(NonGamingProtocol protocol){
        if(protocol.getCommand().equals("invite")){
            showInviteMessage(0,protocol.getUserList()[0]);
        }
    }

    private void switchMethods(InviteACK inviteACK){
        if(inviteACK.getCommand().equals("playerUpdate")){
            inviteACK.getTeamList().toArray();
            Users[] users = new Users[inviteACK.getTeamList().size()];
            users = inviteACK.getTeamList().toArray(users);
            GameLobbyWindow.get().updatePlayerList(users);
        }
    }
}

