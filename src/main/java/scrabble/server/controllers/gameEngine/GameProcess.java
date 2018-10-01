package scrabble.server.controllers.gameEngine;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.ErrorProtocol;
import scrabble.protocols.GamingProtocol.BrickPlacing;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.Pack;
import scrabble.protocols.ScrabbleProtocol;
import scrabble.protocols.serverResponse.GamingSync;
import scrabble.protocols.serverResponse.InviteACK;
import scrabble.protocols.serverResponse.NonGamingResponse;
import scrabble.protocols.serverResponse.VoteRequest;
import scrabble.server.controllers.gameEngine.blockingqueque.EnginePutMsg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameProcess {
    private final int ID_PLACEHOLDER = -1;
    private int gameHost = ID_PLACEHOLDER;
    private boolean gameStart = false;
    private int whoseTurn;
    private int numVoted;
    private int agree;
    private int disagree;
    private int voteInitiator;

    private char[][] board = new char[20][20];

//    private int currentUserID;
//    private String msg;

    private ArrayList<Users> userList;
    private ArrayList<ArrayList<Users>> teamsInWait;
    private ArrayList<Player> playerList;
    private int[] playersID;
    private ArrayList<Users> viewer;
//    private int teamNum;

    private ConcurrentHashMap<Integer, String> db;
    private ConcurrentHashMap<Integer, ArrayList<Users>> teams;
    private BlockingQueue<Pack> queue;


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

    public void addBlockingQueue(BlockingQueue queue){
        this.queue = queue;
    }


//    public void addData(int currentUserID, String msg) {
//        this.currentUserID = currentUserID;
//        this.msg = msg;
//    }


    public void switchProtocols(int currentUserID, String msg) {
        ScrabbleProtocol temp = JSON.parseObject(msg, ScrabbleProtocol.class);
        String type = temp.getTAG();
        switch (type) {
            case "NonGamingProtocol":
                nonGamingOperation(currentUserID, JSON.parseObject(msg, NonGamingProtocol.class));
            case "GamingOperationProtocol":
                gamingOperation(currentUserID, JSON.parseObject(msg, GamingOperationProtocol.class));
            default:
                break;
        }
    }

    private void nonGamingOperation(int currentUserID, NonGamingProtocol nonGamingProtocol) {
        //command: start,login, logout, invite(inviteOperation, inviteResponse)
        String command = nonGamingProtocol.getCommand();
        String[] userList = nonGamingProtocol.getUserList();
        boolean isAccept = nonGamingProtocol.isInviteAccepted();
        int hostID = nonGamingProtocol.getHostID();
        nonGamingOperationExecutor(currentUserID,command, userList, isAccept, hostID);
    }

    private void gamingOperation(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        //command:  brickPlacing, vote
        String command = gamingOperationProtocol.getCommand();
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        int[] start = gamingOperationProtocol.getStartPosition();
        int[] end = gamingOperationProtocol.getEndPosition();
        switch (command) {
            case "vote":
                if (gamingOperationProtocol.isVote()){
                    voteInitiator = currentUserID;
                    board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getbrick());
                    boardUpdate(currentUserID);
                    voteOperation(currentUserID, start, end);
                    waitVoting();

                    voteResult(start,end);
                    gameTurnControl();
                    boardUpdate(currentUserID);
//                    voteInitiator = ID_PLACEHOLDER;
                }else {
                    boardUpdate(currentUserID);
                }
                break;
            case "voteResponse":
                numVoted++;
                playerVoteResponse(gamingOperationProtocol.isVote());
                break;
            case "disconnect":

                break;
            default:
                break;

        }

    }


    private void voteResult(int[] start, int[] end){
        if ((double)agree/disagree >= 0.5){
            //success
            int i;
            for (i=0; i<playerList.size(); i++){
                if (playerList.get(i).getUser().getUserID() == voteInitiator){
                    if (start[0]==end[0]){
                        playerList.get(i).setPoints(end[1]-start[1]+1);
                    }else if (start[1]==end[1]){
                        playerList.get(i).setPoints(end[0]-start[0]+1);
                    }
                    break;
                }
            }
        }else{
            //failure
        }
    }

    private void gameEnd(){

    }

    private void gameTurnControl(){
        if(whoseTurn< playerList.size()){
            whoseTurn ++;
        }else{
            whoseTurn = 1;
        }
    }

    public void waitVoting(){
        while (numVoted != (playerList.size()-1)){
           Pack temp;
           String msg;
           try{
               temp = queue.take();
               switchProtocols(temp.getUserId(), temp.getMsg());
           }catch(InterruptedException e){
               e.printStackTrace();
            }

        }
    }

    private void playerVoteResponse(boolean isVote){
        if (isVote){
            agree++;
        }else{
            disagree++;
        }
    }

    private void voteOperation(int voteInitiator, int[] start, int[] end){
        String command = "voteRequest";
        Pack vote = new Pack(voteInitiator, JSON.toJSONString(new VoteRequest(command, start, end, voteInitiator)));
        vote.setRecipient(playersID);
        EnginePutMsg.getInstance().putMsgToCenter(vote);
    }

    private void nonGamingOperationExecutor(int currentUserID, String command, String[] userList, boolean isAccept, int hostID) {
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
                inviteOperation(currentUserID, userList);
                break;
            case "inviteResponse":
                inviteResponse(currentUserID, hostID, isAccept);
                break;
            default:
                error(currentUserID);
                break;
        }
    }

    //search the index of a user at local memory
    private int userIndexSearch(String userName) {
        int index = 0;
        for (Users user : userList) {
            if (userName.trim().equals(user.getUserName())) {
                break;
            }
            index++;
        }
        return index;
    }

    private int userIndexSearch(int userID) {
        int index = 0;
        for (Users user : userList) {
            if (user.getUserID() == userID) {
                break;
            }
            index++;
        }
        return index;
    }


    private void start(int currentUserID) {
        if (teamsInWait.contains(currentUserID) && !gameStart) {
            gameStart = true;
            gameHost = currentUserID;
            teamStatusUpdate(teamsInWait.get(gameHost), "in-game");
            addPlayers(teamsInWait.get(gameHost));
            whoseTurn = 1;
            boardUpdate(currentUserID);
        } else {
            error(currentUserID);
        }
    }

    private void login(int currentUserID, String userName) {
        if (!db.contains(userName)) {
            db.put(currentUserID, userName);
            userList.add(new Users(currentUserID, userName));
            //send currentUserList back to client
            userListToClient(currentUserID);
        } else {
            error(currentUserID);
        }

    }

    private void logout(int currentUserID) {
        if (gameStart) {
            win(currentUserID);
            userList.remove(userIndexSearch(currentUserID));
            db.remove(currentUserID);
        } else {
            userList.remove(userIndexSearch(currentUserID));
            db.remove(currentUserID);
        }

    }


    private void inviteOperation(int currentUserID, String[] peerList) {
        // initial check the status of user if he or she feels like inviting others
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("available")) {
            ArrayList<Users> team = new ArrayList<>();  // allow multiple teams in wait
            team.add(userList.get(userIndexSearch(currentUserID)));
            userList.get(userIndexSearch(currentUserID)).setStatus("ready"); // status changed when team created
            int hostID = currentUserID;
            teamsInWait.add(team);
            teams.put(hostID, team);

            for (String peer : peerList) {
                if (userList.get(userIndexSearch(peer.trim())).getStatus().equals("available")) {
                    makeEnvelope(currentUserID, peer);
                }
            }
        }
    }

    private void inviteResponse(int currentUserID, int hostID, boolean isAccept) {
        if (isAccept) {
            Users temp = userList.get(userIndexSearch(db.get(currentUserID)));
            teams.get(hostID).add(temp);
            temp.setStatus("ready");

            inviteACK(currentUserID,hostID, isAccept); //ACK to inviteInitiator
            //broadcast to all members of a team
            for (Users peer : teams.get(hostID)) {
                userListToClient(peer.getUserID());
            }
        } else {
            inviteACK(currentUserID,hostID, isAccept);
        }
    }


    private void inviteACK(int currentUserID, int hostID, boolean isAccept) {
        String command = "inviteACK";
        Pack ACK = new Pack(hostID, JSON.toJSONString(new InviteACK(currentUserID, command, isAccept)));
        ACK.setRecipient(null);  //peer-to-peer
        EnginePutMsg.getInstance().putMsgToCenter(ACK);
    }


    private void error(int currentUserID) {
        // userID already exists
        String msg = "Error! This username already exists";  // switch error types
        int errorType = 500; //switch -- (possibly more error types)
        Pack errorMsg = new Pack(currentUserID, JSON.toJSONString(new ErrorProtocol(msg, errorType)));
        EnginePutMsg.getInstance().putMsgToCenter(errorMsg);
    }

    private void makeEnvelope(int currentUserID, String peerName) {
        if (db.contains(peerName)) {
            String command = "invite";
            String[] inviteInitiator = new String[]{db.get(currentUserID)};
            int recipient = ID_PLACEHOLDER;
            for (Users user : userList) {
                if (user.getUserName().equals(peerName)) {
                    recipient = user.getUserID();
                    break;
                }
            }
            String envelope = JSON.toJSONString(new NonGamingProtocol(command, inviteInitiator));
            Pack inviteEnvelope = new Pack(recipient, envelope);
            EnginePutMsg.getInstance().putMsgToCenter(inviteEnvelope);
        }
    }

    private void userListToClient(int userID) {
        Users[] current = new Users[userList.size()];
        String command = "userUpdate";
        Pack list = new Pack(userID, JSON.toJSONString(new NonGamingResponse(current, command)));
        EnginePutMsg.getInstance().putMsgToCenter(list);
    }

    private void teamStatusUpdate(ArrayList<Users> user, String status) {
        for (Users member : user) {
            if (member != null) {
                member.setStatus(status);
            }
        }
    }

    private void addPlayers(ArrayList<Users> readyUser) {
        int sequence = 1;
        playerList = new ArrayList(readyUser.size());
        for (Users member : readyUser) {
            playerList.add(new Player(member, sequence));
            playersID[sequence - 1] = member.getUserID();
            sequence++;
        }

    }

    private void boardUpdate(int currentUserID) {
        String command = "update";
//            Player[] playerArr = playerList.toArray(new Player[playerList.size()]);
        if (playerList != null) {
            Pack update = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, playerList, whoseTurn, board)));
            update.setRecipient(playersID);
            EnginePutMsg.getInstance().putMsgToCenter(update);
        }

    }

    private void win(int currentUserID) {
        String command = "win";
        Collections.sort(playerList);
        int i = 1;
        if (playerList.get(0).getPoints() == playerList.get(i).getPoints()) {
            i++;
        }
        ArrayList<Player> winner = new ArrayList<>(i);
        for (int j = 0; j < i; j++) {
            int numWin = playerList.get(j).getUser().getNumWin();
            playerList.get(j).getUser().setNumWin(++numWin);

            winner.add(playerList.get(j));
        }
        Pack win = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, playerList, whoseTurn, board)));
        win.setRecipient(playersID);
        EnginePutMsg.getInstance().putMsgToCenter(win);
        teamStatusUpdate(teams.get(gameHost), "available");

        //terminate game, reset parameters
        gameStart = false;
        playersID = null;
        playerList = null;
        teamsInWait.remove(teams.get(gameHost));
        teams.remove(gameHost, teams.get(gameHost));
        gameHost = ID_PLACEHOLDER;
    }


}
