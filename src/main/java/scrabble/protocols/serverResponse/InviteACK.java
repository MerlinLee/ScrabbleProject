package scrabble.protocols.serverResponse;

import scrabble.protocols.ScrabbleProtocol;

public class InviteACK extends ScrabbleProtocol {
    private int userID;

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

    public InviteACK(int userID, boolean isAccept) {
        super.setTAG("InviteACK");
        this.userID = userID;
        this.isAccept = isAccept;
    }
}
