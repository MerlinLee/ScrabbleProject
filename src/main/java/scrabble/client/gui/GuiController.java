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
    public GuiController() {
        this.revievePack = -1;
    }

    private int revievePack;
    private String username;

    private GameWindow gameWindow;


    private String status;
    private int seq = -1;
    private String id = new String("None");

    private int currentHostID;
    private volatile static GuiController instance;

    public static synchronized GuiController get() {
        if (instance == null) {
            synchronized (GuiController.class) {
                instance = new GuiController();
            }
        }
        return instance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void sendQuitMsg() {
        if (this.getStatus().equals("in-game")) {
            String command = "quit";
            NonGamingProtocol nonGamingProtocol = new NonGamingProtocol();
            nonGamingProtocol.setCommand(command);
            GuiSender.get().sendToCenter(nonGamingProtocol);
        }
    }

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
        gameWindow = GameWindow.get();
        GameLobbyWindow.get().setModel();
        Thread lobbyThread = new Thread(GameLobbyWindow.get());
        lobbyThread.start();
    }

    public void runGameWindow() {
        //gameWindow = new GameWindow();
        //gameWindow.clearGameWindow();
        Thread gameThread = new Thread(gameWindow);
        gameThread.start();
    }

    /*
        Show Server Response
     */

    void showInviteACK(int id) {
        GameLobbyWindow.get().showRefuseInvite(id);
    }

    synchronized void updateUserList(Users[] userList) {
        // Set user id when first update userList
        if (id.equals("None")) {
            for (Users user : userList) {
                if (user.getUserName().equals(this.username)) {
                    setId(user.getUserID());
                    break;
                }
            }
        }
        if (revievePack == -1) {
            runGameLobbyWindow();
            revievePack++;
        }
//        gameLobbyWindow.updateUserList(userList);
        synchronized (GameLobbyWindow.get()) {
            for (Users user : userList) {
                if (user.getUserName().equals(this.username)) {
                    setStatus(user.getStatus());
                    break;
                }
            }
            GameLobbyWindow.get().updateUserList(userList);
        }
    }

    synchronized void updatePlayerListInLobby(Users[] users) {
        GameLobbyWindow.get().updatePlayerList(users);
    }

    synchronized void updatePlayerListInGame(Player[] playerList) {
        gameWindow.setPlayers(playerList);
        // Set user seq when first update playerList
        if (seq == -1) {
            for (Player player : playerList) {
                if (player.getUser().getUserName().equals(this.username)) {
                    setSeq(player.getInGameSequence());
                    break;
                }
            }
        }

        synchronized (GameLobbyWindow.get()) {
            gameWindow.updatePlayerList(playerList);
        }
    }


    void showInviteMessage(int inviterId, String inviterName) {
        GameLobbyWindow.get().showInviteMessage(inviterId, inviterName);
    }

    void checkIfStartATurn(int seq) {
        //System.err.println("this = " + this.seq + "    " + "The turn = " + seq + "\n");
        gameWindow.setGameTurnTitle(seq);
        if (this.seq == seq) {
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
    }

    void invitePlayers(String[] players) {
        if (this.status.equals("available") || this.id.equals(Integer.toString(currentHostID))){
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteOperation", players);
        GuiSender.get().sendToCenter(nonGamingProtocol);

        // set self as current host
            this.currentHostID = Integer.valueOf(this.id);
        }
    }

    void sendInviteResponse(boolean ack, int inviterId) {
        if (ack) {
            this.currentHostID = inviterId;
        }
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
            brickPlacing.setBrick(String.valueOf(c));
            System.err.println("sendbrick: " + c);
        }
        System.err.println("brickSelf: " + brickPlacing.getBrick());
        String str1 = JSON.toJSONString(brickPlacing);
        System.err.println("brickPlacing: " + str1);
        gamingProtocol = new GamingOperationProtocol("vote", false, brickPlacing, empty, empty);
        String str = JSON.toJSONString(gamingProtocol);
        System.err.println("controller: " + str);
        GuiSender.get().sendToCenter(gamingProtocol);
    }

    void sendVote(int[] lastMove, char c, int sx, int sy, int ex, int ey) {
        if (lastMove != null && c != ' ') {
            BrickPlacing brickPlacing = new BrickPlacing();
            brickPlacing.setBrick(String.valueOf(c));
            brickPlacing.setPosition(lastMove);
            int[] startPosition = new int[2];
            startPosition[0] = sx;
            startPosition[1] = sy;
            int[] endPosition = new int[2];
            endPosition[0] = ex;
            endPosition[1] = ey;
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("vote", true, brickPlacing, startPosition, endPosition);
            GuiSender.get().sendToCenter(gamingProtocol);

            // lock the board after sending vote
            ////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            /////GameGridPanel.get().setAllowDrag(false);
        }
    }

    void sendVoteResponse(boolean vote) {
        BrickPlacing brickPlacing = new BrickPlacing();
        int[] empty = new int[2];
        GamingOperationProtocol gamingProtocol = new GamingOperationProtocol("voteResponse", vote, brickPlacing, empty, empty);
        GuiSender.get().sendToCenter(gamingProtocol);
    }

    public void sendLeaveMsg() {
        if (this.getStatus().equals("ready")) {
            NonGamingProtocol nonGamingProtocol = new NonGamingProtocol();
            nonGamingProtocol.setCommand("leave");
            nonGamingProtocol.setHostID(currentHostID);
            GuiSender.get().sendToCenter(nonGamingProtocol);
        }
    }

    public void resetGame(){
        this.seq = -1;
    }
}

