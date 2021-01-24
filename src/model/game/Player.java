package model.game;

public abstract class Player {
    private int score;
    private Board board;
    private String name;
    private Game currentGame = null;

    public Player(String name) {
        this.score = 0;
        this.board = new Board();
        this.name = name;
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

    public abstract int determineMove();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}
