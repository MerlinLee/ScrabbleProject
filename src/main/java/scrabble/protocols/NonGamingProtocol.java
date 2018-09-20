package scrabble.protocols;
/**
 * Protocols
 *
 * @author Jethro
 * @date 20/09/2018
 */
public class NonGamingProtocol extends ScrabbleProtocol{

    private String command;

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

    private String[] userList;
}
