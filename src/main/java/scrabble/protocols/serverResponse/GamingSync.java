package scrabble.protocols.serverResponse;

import scrabble.Models.Player;
import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

public class GamingSync extends ScrabbleProtocol {
//    private int userID;     -----overlapped with Player
    private String FLAG;

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
    private Player[] playerList;  //previously be Users[] currentUserList

    // new add  -- 9.28
    private char[][] board = new char[20][20];



    // whose turn??  int turn

    public GamingSync(String command, Player[] playerList, char[][] board) {
        super.setTAG("GamingSync");
        this.command = command;
        this.playerList = playerList;
        this.board = board;
    }
}
