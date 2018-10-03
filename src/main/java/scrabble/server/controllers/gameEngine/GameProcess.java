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
    private final int BOARD_SIZE = 20;
    private final int INITIAL_SEQ= 1;

    private int gameHost = ID_PLACEHOLDER;
    private boolean gameStart = false;
    private int whoseTurn;
    private int numVoted;
    private int agree;
    private int disagree;
    private int voteInitiator;
    private int numPass;
    private int gameLoopStartSeq;

    private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

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
        ScrabbleProtocol temp = null;
        if(!msg.equals("null")){
             temp = JSON.parseObject(msg, ScrabbleProtocol.class);
            String type = temp.getTAG();
            switch (type) {
                case "NonGamingProtocol":
                    nonGamingOperation(currentUserID, JSON.parseObject(msg, NonGamingProtocol.class));
                    break;
                case "GamingOperationProtocol":
                    gamingOperation(currentUserID, JSON.parseObject(msg, GamingOperationProtocol.class));
                    break;
                default:
                    break;
            }
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
        //command: vote, voteResponse, disconnect
        String command = gamingOperationProtocol.getCommand();
        switch (command) {
            case "vote":
                if (gamingOperationProtocol.isVote()){
                    hasVote(currentUserID, gamingOperationProtocol);
                }else {
                    hasNotVote(currentUserID, gamingOperationProtocol);
                }
                break;
            case "voteResponse":
                numVoted++;
                playerVoteResponse(gamingOperationProtocol.isVote());
                break;
            case "disconnect":
                if (gameStart == true){
                win(currentUserID);
                    //reset gameEndCheck parameters
                    numPass = 0;
                    gameLoopStartSeq = 0;
                }else{
                    //remove disconnected users
                    db.remove(currentUserID);
                    userList.remove(userIndexSearch(currentUserID));
                }
                break;
            default:
                break;

        }

    }


    private void voteResult(int[] start, int[] end){
        if ((double)agree/disagree >= 0.5){
            //success
            int i;
            int index = playerIndexSearch(voteInitiator);
                if (start[0]==end[0]){
                    playerList.get(index).setPoints(end[1]-start[1]+1);
                }else if (start[1]==end[1]){
                    playerList.get(index).setPoints(end[0]-start[0]+1);
                }
        }else{
            //failure
        }
    }

    //search player instance according to userID
    private int playerIndexSearch(int currentUserID){
        int index;
        for (index = 0; index<playerList.size();index++){
            if(playerList.get(index).getUser().getUserID()==currentUserID){
                break;
            }
        }
        return index;
    }

    private void hasVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol){
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        int[] start = gamingOperationProtocol.getStartPosition();
        int[] end = gamingOperationProtocol.getEndPosition();

        voteInitiator = currentUserID;
        board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getbrick());
        boardUpdate(currentUserID);
        voteOperation(currentUserID, start, end);
        waitVoting();

        voteResult(start,end);
        gameTurnControl();
        boardUpdate(currentUserID);

        //reset gameEndCheck parameters
        numPass = 0;
        gameLoopStartSeq = 0;
    }

    private void hasNotVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol){
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        //initial check gameEnd conditions (1. if every player had a turn -- sequence loop check  2. num of direct pass)
        if (!gameEndCheck(currentUserID)) {
            if (bp.getPosition() != null) {
                gameTurnControl();
                boardUpdate(currentUserID);
            } else {
                int index = playerIndexSearch(currentUserID);
                gameLoopStartSeq = playerList.get(index).getInGameSequence();
                numPass++;
                gameTurnControl();
                boardUpdate(currentUserID);
            }
        }else {
            win(currentUserID);

            //reset gameEndCheck parameters
            numPass = 0;
            gameLoopStartSeq = 0;
        }
    }

    private boolean gameEndCheck(int currentUserID){
        int index = playerIndexSearch(currentUserID);
        if((playerList.get(index).getInGameSequence() == gameLoopStartSeq) && (numPass == playerList.size())){
           return true;  // game should be terminated
        }else{
            return false;  // game should continue
        }
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

    private void nonGamingOperationExecutor(int currentUserID, String command, String[] peerList, boolean isAccept, int hostID) {
        switch (command.trim()) {
            case "start":
                start(currentUserID);
                break;
            case "login":
                login(currentUserID, peerList[0]);
                break;
            case "logout":
                logout(currentUserID);
                break;
            case "inviteOperation":
                inviteOperation(currentUserID, peerList);
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
            //lack check for connected players
            gameStart = true;
            gameHost = currentUserID;
            teamStatusUpdate(onlineCheck(teamsInWait.get(gameHost)), "in-game");
            addPlayers(teamsInWait.get(gameHost));
            whoseTurn = 1;
            boardUpdate(currentUserID);
        } else {
            error(currentUserID);
        }
    }

    private ArrayList<Users> onlineCheck(ArrayList<Users> team){
        for (Users member : team){
            if(!db.contains(member.getUserName())){
                team.remove(member);
            }
        }
        return team;
    }

    private void login(int currentUserID, String userName) {
        // same username, replicated login requests not allowed
        if (!db.contains(userName) && db.get(currentUserID) == null) {
            db.put(currentUserID, userName);
            userList.add(new Users(currentUserID, userName));
            //send currentUserList back to client
            userListToClient();
        } else {
            error(currentUserID);
        }

    }

    private void logout(int currentUserID) {
        if (db.get(currentUserID)!=null) {
            if (gameStart) {
                win(currentUserID);
                userList.remove(userIndexSearch(currentUserID));
                db.remove(currentUserID);
            } else {
                userList.remove(userIndexSearch(currentUserID));
                db.remove(currentUserID);
            }
        }else{
            error(currentUserID);
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
        }else{
            //error
            error(currentUserID);
        }
    }

    private void inviteResponse(int currentUserID, int hostID, boolean isAccept) {
        String command = "inviteACK";
        if (isAccept) {
            Users temp = userList.get(userIndexSearch(db.get(currentUserID)));
            teams.get(hostID).add(temp);
            temp.setStatus("ready");
            int size = teams.get(hostID).size();
            Users[] teamList = teams.get(hostID).toArray(new Users[size]);
            inviteACK(command, currentUserID, hostID, isAccept, teamList); //ACK to inviteInitiator

            //broadcast to all members of a team
            playerUpdate(teamList, hostID, isAccept );
        } else {
            int size = teams.get(hostID).size();
            Users[] teamList = teams.get(hostID).toArray(new Users[size]);
            inviteACK(command, currentUserID, hostID, isAccept, teamList);
        }
    }

    private void playerUpdate(Users[] teamList, int hostID, boolean isAccept){
        String command = "playerUpdate";
        for (int i = 0; i<teamList.length; i++){
            if (teamList[i].getUserID() != hostID){
                inviteACK(command,hostID, teamList[i].getUserID(), isAccept, teamList);
            }
        }
    }


    private void inviteACK(String command, int currentUserID, int hostID, boolean isAccept, Users[] teamList) {
        Pack ACK = new Pack(hostID, JSON.toJSONString(new InviteACK(currentUserID, command, isAccept, teamList)));
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
            Users[] inviteInitiator = new Users[]{userList.get(userIndexSearch(currentUserID))};
            int recipient = ID_PLACEHOLDER;
            for (Users user : userList) {
                if (user.getUserName().equals(peerName)) {
                    recipient = user.getUserID();
                    break;
                }
            }
            String envelope = JSON.toJSONString(new NonGamingResponse(inviteInitiator, command));
            Pack inviteEnvelope = new Pack(recipient, envelope);
            EnginePutMsg.getInstance().putMsgToCenter(inviteEnvelope);
        }
    }

    private void userListToClient() {
        Users[] current = new Users[userList.size()];
        current = userList.toArray(current);
        String command = "userUpdate";
        Pack list = new Pack(0, JSON.toJSONString(new NonGamingResponse(current, command)));
        EnginePutMsg.getInstance().putMsgToCenter(list);
    }

    private void userListToClient(int userID) {
        Users[] current = new Users[userList.size()];
        current = userList.toArray(current);
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
        int sequence = INITIAL_SEQ;
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
            int size = playerList.size();
            Player[] temp = playerList.toArray(new Player[size]);
            Pack update = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board)));
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
        int size = winner.size();
        Player[] temp = winner.toArray(new Player[size]);
        Pack win = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board)));
        win.setRecipient(playersID);   //multi-cast
        EnginePutMsg.getInstance().putMsgToCenter(win);
        teamStatusUpdate(teams.get(gameHost), "available");
        userListToClient();

        //terminate game, reset parameters
        gameStart = false;
        playersID = null;
        playerList = null;
        board = new char[BOARD_SIZE][BOARD_SIZE];
        teamsInWait.remove(teams.get(gameHost));
        teams.remove(gameHost, teams.get(gameHost));
        gameHost = ID_PLACEHOLDER;
    }

}
