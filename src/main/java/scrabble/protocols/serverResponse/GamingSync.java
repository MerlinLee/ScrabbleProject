package scrabble.protocols.serverResponse;

import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

import java.util.ArrayList;

public class GamingSync extends ScrabbleProtocol {
//    private int userID;     -----overlapped with Player
    private String FLAG;

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public int getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    private String command;   // new add -- 9.28

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }
//    public int getUserID() {
//        return userID;
//    }
//
//    public void setUserID(int userID) {
//        this.userID = userID;
//    }

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public boolean[] getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean[] isSuccess) {
        this.isSuccess = isSuccess;
    }

//    public Users[] getCurrentUsersList() {
//        return CurrentUsersList;
//    }
//
//    public void setCurrentUsersList(Users[] currentUsersList) {
//        CurrentUsersList = currentUsersList;
//    }

    //first cell is about successful packet transmission
    //Second cell is about vote result.
    private boolean[] isSuccess = new boolean[2];

    /*Contain score....
    *
    * */
    private ArrayList<Player> playerList;  //previously be Users[] currentUserList

    // new add  -- 9.28
    private char[][] board = new char[20][20];



    // whose turn??  int turn
    private int nextTurn;

    public GamingSync(String command, ArrayList<Player> playerList,int nextTurn, char[][] board) {
        super.setTAG("GamingSync");
        this.nextTurn = nextTurn;
        this.command = command;
        this.playerList = playerList;
        this.board = board;
    }
}
