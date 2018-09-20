package scrabble.protocols.serverResponse;

import scrabble.Models.Users;
import scrabble.protocols.ScrabbleProtocol;

public class GamingResponse extends ScrabbleProtocol {
    private int id;
    private String FLAG;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Users[] getCurrentUsersList() {
        return CurrentUsersList;
    }

    public void setCurrentUsersList(Users[] currentUsersList) {
        CurrentUsersList = currentUsersList;
    }

    //first cell is about successful packet transmission
    //Second cell is about vote result.
    private boolean[] isSuccess = new boolean[2];

    /*Contain score....
    *
    * */
    private Users[] CurrentUsersList;
}
