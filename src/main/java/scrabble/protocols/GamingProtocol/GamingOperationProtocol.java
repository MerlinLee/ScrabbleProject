package scrabble.protocols.GamingProtocol;

import scrabble.protocols.ScrabbleProtocol;

public class GamingOperationProtocol extends ScrabbleProtocol {
    private boolean vote;

    public BrickPlacing getBrickPlacing() {

        return brickPlacing;
    }

    public void setBrickPlacing(BrickPlacing brickPlacing) {
        this.brickPlacing = brickPlacing;
    }

    private BrickPlacing brickPlacing;

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public int[] getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int[] startPosition) {
        this.startPosition = startPosition;
    }

    public int[] getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int[] endPosition) {
        this.endPosition = endPosition;
    }

    private int[] startPosition = new int[2];

    private int[] endPosition = new int[2];
}

