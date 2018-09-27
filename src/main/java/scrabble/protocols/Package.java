package scrabble.protocols;

public class Package {
    private int userId;
    private String msg;

    public int getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }

    public Package(int userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }
}
