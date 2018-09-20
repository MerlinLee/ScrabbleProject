package scrabble.protocols.serverResponse;

import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

public class NonGamingResponse extends ScrabbleProtocol {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users[] getUsersList() {
        return usersList;
    }

    public void setUsersList(Users[] usersList) {
        this.usersList = usersList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Users[] usersList;
    private String status;
}
