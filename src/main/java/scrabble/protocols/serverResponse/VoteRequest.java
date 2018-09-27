package scrabble.protocols.serverResponse;

import scrabble.protocols.ScrabbleProtocol;

/***
 * this class should be VoteResponse
 */

public class VoteRequest extends ScrabbleProtocol {

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

    public int getVoteInitiator() {
        return voteInitiator;
    }

    public void setVoteInitiator(int voteInitiator) {
        this.voteInitiator = voteInitiator;
    }

    private int voteInitiator;
}
