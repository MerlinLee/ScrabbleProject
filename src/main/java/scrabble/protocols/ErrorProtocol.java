package scrabble.protocols;

/**
 * NonGamingProtocols
 *
 * @author Jethro
 * @date 27/09/2018
 */
public class ErrorProtocol extends ScrabbleProtocol {
    private String errorMsg;
    private int errorType; // 500 -- username already exists


    public ErrorProtocol(String errorMsg, int errorType) {
        this.errorMsg = errorMsg;
        this.errorType = errorType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }
}
