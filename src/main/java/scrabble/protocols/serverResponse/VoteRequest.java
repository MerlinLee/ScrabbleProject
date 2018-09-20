package scrabble.protocols.serverResponse;

import scrabble.protocols.ScrabbleProtocol;

public class VoteRequest extends ScrabbleProtocol {
    private int[] startPosition = new int[2];
    private int[] endPosition = new int[2];
}
