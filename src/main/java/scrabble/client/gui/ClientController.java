package scrabble.client.gui;

import scrabble.Models.Users;
import scrabble.protocols.GamingProtocol.BrickPlacing;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;

import java.net.Socket;
import java.net.UnknownHostException;

public class ClientController {

    private String username;
    private String id;

    private GameWindow gameWindow;
    private LoginWindow loginWindow;
    private GameLobbyWindow gameLobbyWindow;
    private ClientListener listener;
    private ClientSender sender;

    private Socket socket;

    ClientController() {
        loginWindow = LoginWindow.get();
        loginWindow.setClient(this);
        Thread loginThread = new Thread(loginWindow);
        loginThread.start();
    }

    public void openSocket(String address, String portStr, String username) {
        try {
            if (address.length() == 0 || portStr.length() == 0 || username.length() == 0) {
                loginWindow.showDialog("Please fill out all fields!");
                return;
            }
            int port = Integer.parseInt(portStr);
            socket = new Socket(address, port);
            this.username = username;
            listenToServer();
            sendToServer();
            loginWindow.closeWindow();
            runGameLobbyWindow();
            loginGame();
        } catch (UnknownHostException e) {
            loginWindow.showDialog("Unkonwn Host!");
        } catch (Exception e) {
            System.out.println(e.toString());
            loginWindow.showDialog("Cannot connect to the server!");
        }
    }

    private void runGameLobbyWindow() {
        gameLobbyWindow = GameLobbyWindow.get();
        gameLobbyWindow.setClient(this);
        gameLobbyWindow.setModel();
        Thread lobbyThread = new Thread(gameLobbyWindow);
        lobbyThread.start();
    }

    private void runGameWindow() {
        gameWindow = GameWindow.get();
        gameWindow.setClient(this);
        Thread gameThread = new Thread(gameWindow);
        gameThread.start();
    }

    void listenToServer() {
        listener = ClientListener.get(socket, this);
        listener.start();
    }

    void sendToServer() {
        sender = ClientSender.get(socket);
        sender.start();
    }

    void startOneTurn() {
        gameWindow.startOneTurn();
    }

    public static void main(String[] args) {
        ClientController client = new ClientController();
    }

    void invitePlayers(String[] players) {
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("invite", players);
        sender.sendToServer(nonGamingProtocol);
    }

    void startGame(String[] players) {
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("start", players);
        sender.sendToServer(nonGamingProtocol);
        runGameWindow();
    }

    void quitGame() {
        String[] selfArray = new String[1];
        selfArray[0] = username;
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("quit", selfArray);
        sender.sendToServer(nonGamingProtocol);
    }

    void loginGame() {
        String[] selfArray = new String[1];
        selfArray[0] = username;
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("login", selfArray);
        sender.sendToServer(nonGamingProtocol);
    }

    void logoutGame() {
        try {
            String[] selfArray = new String[1];
            selfArray[0] = username;
            NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("logout", selfArray);
            sender.sendToServer(nonGamingProtocol);
            socket.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /*
    void placingChar(int[] lastMove, char c) {
        try {
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol();
            gamingProtocol.setUserID(Integer.parseInt(id));
            gamingProtocol.setCharacter(c);
            gamingProtocol.setPosition(lastMove);
            gamingProtocol.setTAG("BrickPlacing");
            sender.sendToServer(gamingProtocol);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    */

    void sendPass(int[] lastMove, char c) {
        try {
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol();
            gamingProtocol.setVote(false);
            BrickPlacing brickPlacing = new BrickPlacing();
            brickPlacing.setbrick(c);
            brickPlacing.setPosition(lastMove);
            gamingProtocol.setBrickPlacing(brickPlacing);
            sender.sendToServer(gamingProtocol);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void sendVote(int[] lastMove, char c, int sx, int sy, int ex, int ey) {
        try {
            GamingOperationProtocol gamingProtocol = new GamingOperationProtocol();
            gamingProtocol.setVote(true);
            BrickPlacing brickPlacing = new BrickPlacing();
            brickPlacing.setbrick(c);
            brickPlacing.setPosition(lastMove);
            gamingProtocol.setBrickPlacing(brickPlacing);
            int[] startPosition = new int[2];
            startPosition[0] = sx;
            startPosition[0] = sy;
            int[] endPosition = new int[2];
            endPosition[0] = ex;
            endPosition[0] = ey;
            gamingProtocol.setStartPosition(startPosition);
            gamingProtocol.setStartPosition(endPosition);
            sender.sendToServer(gamingProtocol);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void showLoginRespond(Users[] users, String status) {
        this.id = Integer.toString(users[0].getUserID());
        gameLobbyWindow.updateUserList(users[0].getUserID(), users[0].getUserName(), users[0].getStatus());
    }

    void showInviteRespond(int id, boolean ac) {
        if (ac) {
            gameLobbyWindow.addToPlayerList(id);
        }
        else {
            gameLobbyWindow.refuseInvite(id);
        }
    }

    String getId() {
        return id;
    }
}

