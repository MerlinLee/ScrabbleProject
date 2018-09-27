package scrabble.protocols.serverResponse;

import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

public class NonGamingResponse extends ScrabbleProtocol {

    // userID is unnecessary
//    private int userID;

    public NonGamingResponse(Users[] usersList, String command) {
        super.setTAG("NonGamingResponse");
//        this.userID = userID;
        this.usersList = usersList;
        this.command = command;
    }

//    public int getUserID() {
//        return userID;
//    }
//
//    public void setUserID(int userID) {
//        this.userID = userID;
//    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Users[] getUsersList() {
        return usersList;
    }

    public void setUsersList(Users[] usersList) {
        this.usersList = usersList;
    }

    private Users[] usersList;
    private String command;
    // 原来有个status
}
