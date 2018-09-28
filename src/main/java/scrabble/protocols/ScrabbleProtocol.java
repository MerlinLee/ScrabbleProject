package scrabble.protocols;
/**
 * Protocols
 *
 * @author Merlin
 * @date 20/09/2018
 */
public class ScrabbleProtocol {

    // GamingOperationProtocol, NonGamingProtocol, ErrorProtocol
    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    private String TAG;
}
