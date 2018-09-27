package scrabble.Models;

public class Player {
    private Users user;
    private int inGameSequence;  // sequence for gaming turns control

    public int getInGameSequence() {
        return inGameSequence;
    }

    public void setInGameSequence(int inGameSequence) {
        this.inGameSequence = inGameSequence;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private int points;   //current points earned during a game

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }



    public Player(Users user, int inGameSequence) {
        this.user = user;
        this.inGameSequence = inGameSequence;
    }
}
