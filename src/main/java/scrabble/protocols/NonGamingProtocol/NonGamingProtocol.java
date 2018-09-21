package scrabble.protocols.NonGamingProtocol;

import scrabble.protocols.ScrabbleProtocol;

/**
 * NonGamingProtocols
 *
 * @author Jethro
 * @date 20/09/2018
 */
public class NonGamingProtocol extends ScrabbleProtocol {
    //start,login, logout, quit, invite
    private String command;
    private String[] userList;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getUserList() {
        return userList;
    }

    public void setUserList(String[] userList) {
        this.userList = userList;
    }

}
