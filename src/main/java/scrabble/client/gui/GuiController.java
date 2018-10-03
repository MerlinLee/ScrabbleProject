package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.GamingProtocol.BrickPlacing;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GuiController {

    private String username;
    private int seq = -1;
    private String id = new String("None");

    private GameWindow gameWindow;
    private GameLobbyWindow gameLobbyWindow;

    private volatile static GuiController instance;

    public static synchronized GuiController get() {
        if (instance == null) {
            synchronized (GuiController.class){
                instance = new GuiController();
            }
        }
        return instance;
    }

    /*
    ClientController() {
        loginWindow = LoginWindow.get();
        loginWindow.setClient(this);
        Thread loginThread = new Thread(loginWindow);
        loginThread.start();
    }
    */

    void setUserName(String username) {
        this.username = username;
    }

    void setId(int id) {
        this.id = Integer.toString(id);
    }

    void setSeq(int seq) {
        this.seq = seq;
    }

    String getUsername() {
        return username;
    }

    int getSeq() {
        return seq;
    }

    String getId() {
        return id;
    }

    private void runGameLobbyWindow() {
        gameLobbyWindow = GameLobbyWindow.get();
        gameLobbyWindow.setModel();
        Thread lobbyThread = new Thread(gameLobbyWindow);
        lobbyThread.start();
    }

    public void runGameWindow() {
        gameWindow = GameWindow.get();
        Thread gameThread = new Thread(gameWindow);
        gameThread.start();
    }

    /*
    void quitGame() {
        String[] selfArray = new String[1];
        selfArray[0] = username;
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("quit", selfArray);
        GuiSender.get().sendToCenter(nonGamingProtocol);
    }
    */

    /*
        Show Server Response
     */

    void showInviteACK(int id) {
        gameLobbyWindow.showRefuseInvite(id);
    }

    void updateUserList(Users[] userList) {
        // Set user id when first update userList
        if (id.equals("None")) {
            for (Users user: userList) {
                if (user.getUserName().equals(this.username)) {
                    setId(user.getUserID());
                    break;
                }
            }
        }
        gameLobbyWindow.updateUserList(userList);
    }

    void updatePlayerListInLobby(Users[] users) {
        gameLobbyWindow.updatePlayerList(users);
    }

    void updatePlayerListInGame(Player[] playerList) {
        // Set user seq when first update playerList
        if (seq == -1) {
            for (Player player: playerList) {
                if (player.getUser().getUserName().equals(this.username)) {
                    setSeq(player.getInGameSequence());
                    break;
                }
            }
        }
        gameWindow.updatePlayerList(playerList);
    }

    void showInviteMessage(int inviterId, String inviterName) {
        gameLobbyWindow.showInviteMessage(inviterId, inviterName);
    }

    void checkIfStartATurn(int seq) {
        System.err.println("this = "+ this.seq + "    " + "The turn = " + seq);
        if (this.seq == seq) {
            System.err.println("My turn");
            gameWindow.startOneTurn();
        }
    }

    void updateBoard(char[][] board) {
        gameWindow.updateBoard(board);
    }

    void showWinners(Player[] players) {
        setSeq(-1);
        gameWindow.showWinners(players);
    }

    void showVoteRequest(int inviterId, int[] startPosition, int[] endPosition) {
        gameWindow.showVoteRequest(inviterId, startPosition, endPosition);
    }

    /*
        Send to Center
     */

    void loginGame() {
        String[] selfArray = new String[1];
        selfArray[0] = username;
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("login", selfArray);
        GuiSender.get().sendToCenter(nonGamingProtocol);
        runGameLobbyWindow();
    }

    void invitePlayers(String[] players) {
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteOperation", players);
        GuiSender.get().sendToCenter(nonGamingProtocol);
    }

    void sendInviteResponse(boolean ack, int inviterId) {
        String[] userList = new String[1];
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteResponse", userList);
        nonGamingProtocol.setInviteAccepted(ack);
        nonGamingProtocol.setHostID(inviterId);
        GuiSender.get().sendToCenter(nonGamingProtocol);
    }

    void logoutGame() {
        String[] emptyArray = new String[1];
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("logout", emptyArray);
        GuiSender.get().sendToCenter(nonGamingProtocol);
    }

    void startGame() {
        String[] emptyArray = new String[1];
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("start", emptyArray);
        GuiSender.get().sendToCenter(nonGamingProtocol);
//        runGameWindow();
    }

    void sendPass(int[] lastMove, char c) {
        int[] empty = new int[2];
        GamingOperationProtocol gamingProtocol;
        BrickPlacing brickPlacing = new BrickPlacing();
        // Placing but pass
        if (lastMove[0] != -1 && lastMove[1] != -1) {
            brickPlacing.setPosition(lastMove);
            brickPlacing.setbrick(Character.toString(c));
            System.err.println("sendbrick: " + c);
        }
        System.err.println("brickSelf: " + brickPlacing.getbrick());
        String str1 = JSON.toJSONString(brickPlacing);
        System.err.println("brickPlacing: " + str1);
        gamingProtocol = new GamingOperationProtocol("vote", false, brickPlacing, empty, empty);
        String str = JSON.toJSONString(gamingProtocol);
        System.err.println("controller: " + str);
        GuiSender.get().sendToCenter(gamingProtocol);
    }

    void sendVote(int[] lastMove, char c, int sx, int sy, int ex, int ey) {
        BrickPlacing brickPlacing = new BrickPlacing();
        brickPlacing.setbrick(Character.toString(c));
        brickPlacing.setPosition(lastMove);
        int[] startPosition = new int[2];
        startPosition[0] = sx;
        startPosition[0] = sy;
        int[] endPosition = new int[2];
        endPosition[0] = ex;
        endPosition[0] = ey;
        GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("vote", true, brickPlacing, startPosition, endPosition);
        GuiSender.get().sendToCenter(gamingProtocol);
    }

    void sendVoteResponse(boolean vote) {
        BrickPlacing brickPlacing = new BrickPlacing();
        int[] empty = new int[2];
        GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("voteResponse", vote, brickPlacing, empty, empty);
        GuiSender.get().sendToCenter(gamingProtocol);
    }
}

