package scrabble.Models;

public class Player {
    private Users user;
    private int userId;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Player(Users user, int userId) {
        this.user = user;
        this.userId = userId;
    }
}
