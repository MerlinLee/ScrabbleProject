package scrabble.server.controllers.gameEngine;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.serverResponse.ErrorProtocol;
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
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class GameProcess {
    private final int ID_PLACEHOLDER = -1;
    private final int BOARD_SIZE = 20;
    private final int INITIAL_SEQ = 1;

    private int gameHost = ID_PLACEHOLDER;
    private boolean gameStart = false;
    private int whoseTurn;
    private int numVoted;
    private int agree;
    private int disagree;
    private int voteInitiator;
    private boolean voteSuccess;
    private int numPass;
    private int gameLoopStartSeq;

    private char[][] board;

//    private int currentUserID;
//    private String msg;

    private ArrayList<Users> userList;
    private ArrayList<ArrayList<Users>> teamsInWait;
    private ArrayList<Player> playerList;
    private int[] playersID;
//    private int teamNum;

    private ConcurrentHashMap<Integer, String> db;
    private ConcurrentHashMap<Integer, ArrayList<Users>> teams;
    private BlockingQueue<Pack> queue;


    private volatile static GameProcess gameProcess;


    public GameProcess() {
        teamsInWait = new ArrayList<>();
        userList = new ArrayList<>();
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

    public void addBlockingQueue(BlockingQueue queue) {
        this.queue = queue;
    }


//    public void addData(int currentUserID, String msg) {
//        this.currentUserID = currentUserID;
//        this.msg = msg;
//    }


    public void switchProtocols(int currentUserID, String msg) {
        ScrabbleProtocol temp = null;
        if (!msg.equals("null")) {
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
                    userListToClient();
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
        nonGamingOperationExecutor(currentUserID, command, userList, isAccept, hostID);
    }

    private void gamingOperation(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        //command: vote, voteResponse, disconnect
        String command = gamingOperationProtocol.getCommand();
        switch (command) {
            case "vote":
                if (gameStart && (whoseTurn == playerList
                        .get(playerIndexSearch(currentUserID))
                        .getInGameSequence())) {
                    if (gamingOperationProtocol.isVote()) {
                        hasVote(currentUserID, gamingOperationProtocol);
                    } else {
                        hasNotVote(currentUserID, gamingOperationProtocol);
                    }
                } else {
                    //ignore
                }
                break;
            case "voteResponse":
                if (gameStart) {
                    numVoted++;
                    playerVoteResponse(gamingOperationProtocol.isVote());
                }
                break;
            case "disconnect":
                disconnect(currentUserID);
                break;
            default:
                break;

        }

    }

    private synchronized void userRemove(Users user) {
        if (userList.contains(user)) {
            db.remove(user.getUserID(), user.getUserName());
            userList.remove(user);
        }
    }

    private void disconnect(int currentUserID) {
        if (gameStart == true) {
            if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                playerList.remove(playerIndexSearch(currentUserID));
                win(currentUserID);
            }
            //remove disconnected users
//            if (db.containsKey(currentUserID)) {
//                db.remove(currentUserID);
//                userList.remove(userIndexSearch(currentUserID));
//            }
            userRemove(userList.get(userIndexSearch(currentUserID)));
            userListToClient();

            //reset gameEndCheck parameters
            numPass = 0;
//            gameLoopStartSeq = 0;
        } else if (db.containsKey(currentUserID)) {
            //remove disconnected users
            userRemove(userList.get(userIndexSearch(currentUserID)));
//            if (db.containsKey(currentUserID)) {
//                db.remove(currentUserID);
//                userList.remove(userIndexSearch(currentUserID));
//            }
            userListToClient();
        } else {
            //Not exist
        }
    }


    private void voteResult(int[] start, int[] end) {
        if ((double) agree / numVoted > 0.5) {
            //success
            int i;
            voteSuccess = true;
            int index = playerIndexSearch(voteInitiator);
            int currentPoints = playerList.get(index).getPoints();
            if (start[0] == end[0]) {
                playerList.get(index).setPoints(end[1] - start[1] + 1 + currentPoints);
            } else if (start[1] == end[1]) {
                playerList.get(index).setPoints(end[0] - start[0] + 1 + currentPoints);
            }
        } else {
            //failure
            voteSuccess = false;
        }

        //reset agree/disagree num
        voteInitiator = ID_PLACEHOLDER;
        numVoted = 0;
        agree = 0;
        disagree = 0;
    }

    //search player instance according to userID
    private int playerIndexSearch(int currentUserID) {
        int index;
        for (index = 0; index < playerList.size(); index++) {
            if (playerList.get(index).getUser().getUserID() == currentUserID) {
                break;
            }
        }
        return index;
    }

    private void hasVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        int[] start = gamingOperationProtocol.getStartPosition();
        int[] end = gamingOperationProtocol.getEndPosition();

        //reset gameEndCheck parameters
        numPass = 0;
//        gameLoopStartSeq = 0;

        voteInitiator = currentUserID;
        board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getBrick().charAt(0));
        boardUpdate(currentUserID);
        voteOperation(currentUserID, start, end);
        waitVoting();

        voteResult(start, end);
        gameTurnControl();
        boardUpdate(currentUserID);

        //reset voteSuccess
        voteSuccess = false;
    }

    private void hasNotVote(int currentUserID, GamingOperationProtocol gamingOperationProtocol) {
        BrickPlacing bp = gamingOperationProtocol.getBrickPlacing();
        //initial check gameEnd conditions (1. if every player had a turn -- sequence loop check  2. num of direct pass)
        if (bp.getBrick() != null) {
            //reset gameEndCheck parameters
            numPass = 0;

            board[bp.getPosition()[0]][bp.getPosition()[1]] = Character.toUpperCase(bp.getBrick().charAt(0));
            gameTurnControl();
            boardUpdate(currentUserID);
        } else {
            numPass++;
            if (!gameEndCheck()) {
                gameTurnControl();
                boardUpdate(currentUserID);
            } else {
                win(currentUserID);
                //reset gameEndCheck parameters
                numPass = 0;
            }
        }
    }


    private boolean gameEndCheck() {
//        int index = playerIndexSearch(currentUserID);
        if (numPass == playerList.size()) {
            return true;  // game should be terminated
        } else {
            return false;  // game should continue
        }
    }

    private void gameTurnControl() {
        if (whoseTurn < playerList.size()) {
            whoseTurn++;
        } else {
            whoseTurn = 1;
        }
    }

    public void waitVoting() {
        System.out.println("START WAITING: " + numVoted);
        while (numVoted != (playerList.size())) {
            Pack temp;
            try {
                temp = queue.take();
                System.out.println(temp.getMsg());
                switchProtocols(temp.getUserId(), temp.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("WAITING END: " + numVoted);
    }

    private void playerVoteResponse(boolean isVote) {
        if (isVote) {
            agree++;
        } else {
            disagree++;
        }
    }

    private void voteOperation(int voteInitiator, int[] start, int[] end) {
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
            case "quit":
                if (gameStart == true) {
                    if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                        playerList.remove(playerIndexSearch(currentUserID));
                        win(currentUserID);
                    }
                }
            case "leave":
                leaveTeam(currentUserID, hostID);
                break;
            default:
                error(currentUserID, "Unknown Error", "lobby");
                break;
        }
    }

    private void leaveTeam(int currentUserID, int hostID) {
        int index = userIndexSearch(currentUserID);
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("ready")) {
            if (currentUserID == hostID) {
                //team host leaves
                for (Users member : teams.get(currentUserID)) {
                    teamUpdate(member.getUserID());
                }
                teamStatusUpdate(teams.get(currentUserID), "available");
                userListToClient();
                teamsInWait.remove(teams.get(currentUserID));
                teams.remove(currentUserID);
            } else {
                // other team members leave
                if (teams.containsKey(hostID)) {
                    ArrayList<Users> team = teams.get(hostID);
                    if (team.contains(userList.get(index))) {
                        team.remove(userList.get(index));
                        userList.get(index).setStatus("available");
                    }
                    Users[] temp = team.toArray(new Users[team.size()]);

                    teamUpdate(currentUserID);
                    teamUpdate(temp, hostID, false);
                    userListToClient();
                } else {
                    if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("ready")) {
                        error(currentUserID, "Unknown team", "lobby");
                    }
                    userListToClient();
                }
            }
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

    private void boardInitiation() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void start(int currentUserID) {
        // a game can be started only when team exists (same as Host check) and there is no game in process
        if (teamsInWait.contains(teams.get(currentUserID)) && !gameStart) {

            //initiate game board
            boardInitiation();
            gameHost = currentUserID;
            ArrayList<Users> team = null;
            try {
                team = onlineCheck(teams.get(gameHost));
            } catch (Exception e) {

            }

            //playerID assigned here
            if (addPlayers(team)) {
                teamStatusUpdate(team, "in-game");

                gameStart = true;
                whoseTurn = 1;

                boardUpdate(playersID);
                boardUpdate(currentUserID);
            } else {
                error(currentUserID, "Start Failed! Active team members should be no less than 2", "lobby");
                userListToClient();
            }

            //broadcast to all online users to update status
            userListToClient();
        } else {
            error(currentUserID, "Not authorized to start game", "lobby");
        }
    }

    private synchronized ArrayList<Users> onlineCheck(ArrayList<Users> team) {
        for (Users member : team) {
            if (member == null || (!userList.contains(member))) {
                team.remove(member);
            }
        }
        teamUpdate(team.toArray(new Users[team.size()]), ID_PLACEHOLDER, false);
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
            error(currentUserID, "User already Exists", "login");
        }

    }

    private void logout(int currentUserID) {
        if (db.get(currentUserID) != null) {
            if (gameStart) {
//                userList.remove(userIndexSearch(currentUserID));
//                db.remove(currentUserID);
                if (playerList.get(playerIndexSearch(currentUserID)) != null) {
                    playerList.remove(playerIndexSearch(currentUserID));
                    win(currentUserID);
                }
                userRemove(userList.get(userIndexSearch(currentUserID)));
                userListToClient();
            } else {
//                userList.remove(userIndexSearch(currentUserID));
//                db.remove(currentUserID);
                userRemove(userList.get(userIndexSearch(currentUserID)));
                userListToClient();
            }
        } else {
            error(currentUserID, "No such user", "lobby");
        }
    }


    private void inviteOperation(int currentUserID, String[] peerList) {
        // initial check the status of user if he or she feels like inviting others
        // also check if he or she has already created a team
        if (userList.get(userIndexSearch(currentUserID)).getStatus().equals("available") || teams.containsKey(currentUserID)) {
            if (!teams.containsKey(currentUserID)) {
                ArrayList<Users> team = new ArrayList<>();  // allow multiple teams in wait
                team.add(userList.get(userIndexSearch(currentUserID)));
                userList.get(userIndexSearch(currentUserID)).setStatus("ready"); // status changed when team created
                int hostID = currentUserID;
                teamsInWait.add(team);
                teams.put(hostID, team);
            }

            //make envelope and start inviting
            for (String peer : peerList) {
                if (userList.get(userIndexSearch(peer.trim())).getStatus().equals("available")) {
                    makeEnvelope(currentUserID, peer);
                }
            }
        } else {
            //error, no access error
            error(currentUserID, "No Access to invite others", "lobby");
            userListToClient();
        }
    }

    private synchronized void inviteResponse(int currentUserID, int hostID, boolean isAccept) {
        String command = "inviteACK";
        if (isAccept) {
            Users temp = userList.get(userIndexSearch(db.get(currentUserID)));
            if (!teams.get(hostID).contains(temp) && temp.getStatus().equals("available")) {
                teams.get(hostID).add(temp);
                temp.setStatus("ready");
            }
            int size = teams.get(hostID).size();
            Users[] teamList = teams.get(hostID).toArray(new Users[size]);
            inviteACK(command, currentUserID, hostID, isAccept, teamList); //ACK to inviteInitiator

            //broadcast to all members of a team
            teamUpdate(teamList, hostID, isAccept);

            //broadcast to all users to update status
            userListToClient();
        } else {
            if (teams.containsKey(hostID)) {
                int size = teams.get(hostID).size();
                Users[] teamList = teams.get(hostID).toArray(new Users[size]);
                inviteACK(command, currentUserID, hostID, isAccept, teamList);
            }
            userListToClient();
        }
    }

    private void teamUpdate(Users[] teamList, int hostID, boolean isAccept) {
        String command = "teamUpdate";
        for (int i = 0; i < teamList.length; i++) {
            inviteACK(command, hostID, teamList[i].getUserID(), isAccept, teamList);
        }
    }

    private void teamUpdate(int currentUserID) {
        String command = "teamUpdate";
        inviteACK(command, currentUserID, currentUserID, false, null);
    }


    private void inviteACK(String command, int currentUserID, int hostID, boolean isAccept, Users[] teamList) {
        Pack ACK = new Pack(hostID, JSON.toJSONString(new InviteACK(currentUserID, command, isAccept, teamList)));
        ACK.setRecipient(null);  //peer-to-peer
        EnginePutMsg.getInstance().putMsgToCenter(ACK);
    }


    private void error(int currentUserID, String msg, String type) {
        Pack errorMsg = new Pack(currentUserID, JSON.toJSONString(new ErrorProtocol(msg, type)));
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
        if (user != null) {
            for (Users member : user) {
                if (member != null) {
                    member.setStatus(status);
                }
            }
        }
    }

    private boolean addPlayers(ArrayList<Users> readyUser) {
        if (readyUser.size() >= 2) {
            int sequence = INITIAL_SEQ;
            playerList = new ArrayList<>();
            playersID = new int[readyUser.size()];
            for (Users member : readyUser) {
                playerList.add(new Player(member, sequence));
                playersID[sequence - 1] = member.getUserID();
                sequence++;
            }
            return true;
        } else {
            return false;
        }

    }

    private void boardUpdate(int currentUserID) {
        String command = "update";
//            Player[] playerArr = playerList.toArray(new Player[playerList.size()]);
        if (playerList != null) {
            int size = playerList.size();
            Player[] temp = playerList.toArray(new Player[size]);
            Pack update = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
            update.setRecipient(playersID);
            EnginePutMsg.getInstance().putMsgToCenter(update);
        }

    }

    private void boardUpdate(int[] playersID) {
        String command = "start";
//            Player[] playerArr = playerList.toArray(new Player[playerList.size()]);
        if (playerList != null) {
            int size = playerList.size();
            Player[] temp = playerList.toArray(new Player[size]);
            Pack update = new Pack(ID_PLACEHOLDER, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
            update.setRecipient(playersID);
            EnginePutMsg.getInstance().putMsgToCenter(update);
            boardUpdate(ID_PLACEHOLDER);
        }

    }

    private void win(int currentUserID) {
        String command = "win";
        Collections.sort(playerList);
        int hi = playerList.size() - 1;
        int i;
        for (i = hi - 1; i >= 0; i--)
            if (playerList.get(hi).getPoints() != playerList.get(i).getPoints()) {
                break;
            }
        ArrayList<Player> winner = new ArrayList<>();
        for (int j = hi; j > i; j--) {
            int numWin = playerList.get(j).getUser().getNumWin();
            playerList.get(j).getUser().setNumWin(++numWin);
            winner.add(playerList.get(j));
        }
        int size = winner.size();
        Player[] temp = winner.toArray(new Player[size]);
        Pack win = new Pack(currentUserID, JSON.toJSONString(new GamingSync(command, temp, whoseTurn, board, voteSuccess)));
        win.setRecipient(playersID);   //multi-cast
        EnginePutMsg.getInstance().putMsgToCenter(win);
        teamStatusUpdate(teams.get(gameHost), "available");
        userListToClient();

        //terminate game, reset parameters
        gameStart = false;
        playersID = null;
        playerList.clear();
        whoseTurn = INITIAL_SEQ;

        boardInitiation();

        if (teams.containsKey(gameHost)) {
            teamsInWait.remove(teams.get(gameHost));
            teams.remove(gameHost, teams.get(gameHost));
            gameHost = ID_PLACEHOLDER;
        }
    }

}
