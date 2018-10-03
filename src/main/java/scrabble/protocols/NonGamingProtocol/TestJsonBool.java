package scrabble.protocols.NonGamingProtocol;

public class TestJsonBool {
    private int id;
    private boolean flag;
    private boolean inviteAccepted;

    public boolean isInviteAccepted() {
        return inviteAccepted;
    }

    public void setInviteAccepted(boolean inviteAccepted) {
        this.inviteAccepted = inviteAccepted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
