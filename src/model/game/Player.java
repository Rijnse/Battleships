package model.game;

public abstract class Player {
    private String name;
    private int score;
    private Board board;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.board = new Board();
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }
}
