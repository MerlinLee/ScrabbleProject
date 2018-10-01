package scrabble.protocols.GamingProtocol;

public class BrickPlacing {
    private int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public char getbrick() {
        return brick;
    }

    public void setbrick(char character) {
        this.brick = brick;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    private char brick;
    private int[] position = new int[2];
}
