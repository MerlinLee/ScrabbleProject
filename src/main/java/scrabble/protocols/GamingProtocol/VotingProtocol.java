package scrabble.protocols.GamingProtocol;

public class VotingProtocol {
    private boolean vote;

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    private boolean pass;

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

