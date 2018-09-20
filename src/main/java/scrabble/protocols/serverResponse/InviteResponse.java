package scrabble.protocols.serverResponse;

import scrabble.protocols.ScrabbleProtocol;

public class InviteResponse extends ScrabbleProtocol {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

    //waiting, accept, reject
    private boolean isAccept;

}
