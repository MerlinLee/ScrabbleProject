package scrabble.server.controllers.gameEngine;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.ErrorProtocol;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.protocols.serverResponse.GamingSync;
import scrabble.protocols.serverResponse.InviteACK;
import scrabble.protocols.serverResponse.NonGamingResponse;
import scrabble.server.controllers.gameEngine.blockingqueque.EnginePutMsg;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameProcess {
    private final int ID_PLACEHOLDER = -1;
    private boolean gameStart = false;

    private char[][] board = new char[20][20];

    private int currentUserID;
    private String msg;

    private ArrayList<Users> userList;
    private ArrayList<ArrayList<Users>> teamsInWait;
    private Player[] playerList;
    private ArrayList<Users> viewer;
//    private int teamNum;

    private ConcurrentHashMap<Integer, String> db;
    private ConcurrentHashMap<Integer, ArrayList<Users>> teams;


    private volatile static GameProcess gameProcess;


    public GameProcess() {
        teamsInWait = new ArrayList<>();
        userList = new ArrayList<>();
        viewer = new ArrayList<>();
        db = new ConcurrentHashMap<>();
        teams = new ConcurrentHashMap<>();
    }

    //Singleton GameProcess
    public static GameProcess getInstance() {
        if (gameProcess == null) {
            synchronized (GameProcess.class) {
                if (gameProcess == null) {
                    gameProcess = new GameProcess();
                }
            }
        }
        return gameProcess;
    }


    public void addData(int currentUserID, String msg) {
        this.currentUserID = currentUserID;
        this.msg=msg;
    }


    public void switchProtocols() {
        ScrabbleProtocol temp = JSON.parseObject(msg, ScrabbleProtocol.class);
        String type = temp.getTAG();
        switch (type) {
            case "NonGamingProtocol":
                nonGamingOperation(JSON.parseObject(msg, NonGamingProtocol.class));
            case "GamingOperationProtocol":
                gamingOperation(JSON.parseObject(msg, GamingOperationProtocol.class));
            default:
                break;
        }
    }

    private void nonGamingOperation(NonGamingProtocol nonGamingProtocol) {
        //command: start,login, logout, invite(inviteOperation, inviteResponse)
        String command = nonGamingProtocol.getCommand();
        String[] userList = nonGamingProtocol.getUserList();
        boolean isAccept = nonGamingProtocol.isInviteAccepted();
        int hostID = nonGamingProtocol.getHostID();
        nonGamingOperationExecutor(command, userList, isAccept, hostID);
    }

    private void gamingOperation(GamingOperationProtocol gamingOperationProtocol) {
        //command:  brickPlacing, vote

    }

    private void nonGamingOperationExecutor(String command, String[] userList, boolean isAccept, int hostID) {
        switch (command.trim()) {
            case "start":
                start(currentUserID);
                break;
            case "login":
                login(currentUserID, userList[0]);
                break;
            case "logout":
                logout(currentUserID);
                break;
            case "inviteOperation":
                inviteOperation(userList);
                break;
            case "inviteResponse":
                inviteResponse(hostID, isAccept);
                break;
            default:
                error(currentUserID);
                break;
        }
    }

    //search the index of a user at local memory
    private int userIndexSearch(String userName){
        int index=0;
        for (Users user:userList){
            if(userName.trim().equals(user.getUserName())){
                break;
            }
            index++;
        }
        return index;
    }

    private int userIndexSearch(int userID){
        int index=0;
        for (Users user:userList){
            if(user.getUserID() == userID){
                break;
            }
            index++;
        }
        return index;
    }


    private void start(int currentUserID) {
        if(teamsInWait.contains(currentUserID) && !gameStart){
            gameStart = true;
            teamStatusUpdate(teamsInWait.get(currentUserID), "in-game");
            addPlayers(teamsInWait.get(currentUserID));
            boardUpdate();
        }else{
            error(currentUserID);
        }
    }

    private void login(int currentUserID, String userName) {
        if (!db.contains(userName)) {
            db.put(currentUserID, userName);
            userList.add(new Users(currentUserID, userName));
            //send currentUserList back to client
            userListToClient(currentUserID);
        }else{
            error(currentUserID);
        }

    }

    private void logout(int currentUserID) {
        if(gameStart){
            win();
            userList.remove(userIndexSearch(currentUserID));
            db.remove(currentUserID);
            gameStart = false;
        }else{
            userList.remove(userIndexSearch(currentUserID));
            db.remove(currentUserID);
        }

    }


    private void inviteOperation(String[] peerList) {
        // initial check the status of user if he or she feels like inviting others
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("available")) {
            ArrayList<Users> team = new ArrayList<>();  // allow multiple teams in wait
            team.add(userList.get(userIndexSearch(currentUserID)));
            userList.get(userIndexSearch(currentUserID)).setStatus("Ready"); // status changed when team created
            int hostID = currentUserID;
            teamsInWait.add(team);
            teams.put(hostID, team);

            for (String peer : peerList) {
                if (userList.get(userIndexSearch(peer.trim())).getStatus().equals("available")){
                    makeEnvelope(peer);
                }
            }
        }
    }

    private void inviteResponse(int hostID, boolean isAccept) {
        if (isAccept){
            Users temp = userList.get(userIndexSearch(db.get(currentUserID)));
            teams.get(hostID).add(temp);
            temp.setStatus("Ready");

            inviteACK(hostID,isAccept); //ACK to inviteInitiator
            //broadcast to all members of a team
            for (Users peer : teams.get(hostID)){
            userListToClient(peer.getUserID());
            }
        }else {
            inviteACK(hostID, isAccept);
        }
    }


    private void inviteACK(int hostID, boolean isAccept) {
        String command = "inviteACK";
        Pack ACK = new Pack(hostID, JSON.toJSONString(new InviteACK(currentUserID, isAccept)));
        EnginePutMsg.getInstance().putMsgToCenter(ACK);
    }


    private void error(int currentUserID){
        // userID already exists
        String msg = "Error! This username already exists";  // switch error types
        int errorType = 500; //switch -- (possibly more error types)
        Pack errorMsg = new Pack(currentUserID, JSON.toJSONString(new ErrorProtocol(msg,errorType)));
        EnginePutMsg.getInstance().putMsgToCenter(errorMsg);
    }

    private void makeEnvelope(String peerName){
        if(db.contains(peerName)){
            String command = "invite";
            String[] inviteInitiator = new String[] {db.get(currentUserID)};
            int recipient = ID_PLACEHOLDER;
            for (Users user : userList){
                if (user.getUserName().equals(peerName)){
                    recipient = user.getUserID();
                    break;
                }
            }
            String envelope = JSON.toJSONString(new NonGamingProtocol(command, inviteInitiator));
            Pack inviteEnvelope = new Pack(recipient, envelope);
            EnginePutMsg.getInstance().putMsgToCenter(inviteEnvelope);
        }
    }

    private void userListToClient(int userID){
        Users[] current = new Users[userList.size()];
        String command = "userUpdate";
        Pack list = new Pack(userID, JSON.toJSONString(new NonGamingResponse(current, command)));
        EnginePutMsg.getInstance().putMsgToCenter(list);
    }

    private void teamStatusUpdate(ArrayList<Users> user, String status){
        for(Users member : user ){
            if (member != null) {
                member.setStatus(status);
            }
        }
    }

    private void addPlayers(ArrayList<Users> readyUser){
        int sequence = 1;
        playerList = new Player[readyUser.size()];
        for (Users member : readyUser){
            playerList[sequence-1] = new Player(member, sequence);
            sequence++;
        }
    }

    private void boardUpdate(){
        for (Player player : playerList){
            String command = "boardUpdate";
            if (player != null){
            Pack update = new Pack(player.getUser().getUserID(), JSON.toJSONString(new GamingSync(command, playerList, board)));
            EnginePutMsg.getInstance().putMsgToCenter(update);
            }
        }
    }

    private void win(){

    }

}
