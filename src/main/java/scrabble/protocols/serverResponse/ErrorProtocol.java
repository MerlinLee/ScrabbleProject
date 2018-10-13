package scrabble.protocols.serverResponse;

import scrabble.protocols.ScrabbleProtocol;

/**
 * NonGamingProtocols
 *
 * @author Jethro
 * @date 27/09/2018
 */
public class ErrorProtocol extends ScrabbleProtocol {
    private String errorMsg;
    private String errorType;


    public ErrorProtocol(){
        super.setTAG("ErrorProtocol");
    }
    public ErrorProtocol(String errorMsg, String errorType) {
        super.setTAG("ErrorProtocol");
        this.errorMsg = errorMsg;
        this.errorType = errorType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
