package model.game;

public abstract class Player {
    private int score;
    private Board board;

    public Player() {
        this.score = 0;
        this.board = new Board();
    }

    public int getScore() {
        return this.score;
    }

    public void incrementScore(int increment) {
        this.score = this.score + increment;
    }

    public Board getBoard() {
        return this.board;
    }
}
