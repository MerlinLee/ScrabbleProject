package scrabble.protocols.serverResponse;

import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

import java.util.ArrayList;

public class InviteACK extends ScrabbleProtocol {
    private String command; //inviteACK, playerUpdate
    private int userID;
    private ArrayList<Users> teamList;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public ArrayList<Users> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<Users> teamList) {
        this.teamList = teamList;
    }

    public int getId() {
        return userID;
    }

    public void setId(int userID) {
        this.userID = userID;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    //accept, reject
    private boolean isAccept;

    public InviteACK(int userID, String command, boolean isAccept, ArrayList<Users> teamList) {
        super.setTAG("InviteACK");
        this.teamList = teamList;
        this.command = command;
        this.userID = userID;
        this.isAccept = isAccept;
    }
}
