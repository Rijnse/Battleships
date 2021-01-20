package model.game;

public class Game {
    private Player one;
    private Player two;

    public Game(Player one, Player two) {
        this.one = one;
        this.two = two;
    }

    public Game(Player one) {
        this.one = one;
        this.two = new HumanPlayer("Waiting for player...");
    }

    public Player getPlayerOne() {
        return one;
    }

    public void setPlayerOne(Player one) {
        this.one = one;
    }

    public Player getPlayerTwo() {
        return two;
    }

    public void setPlayerTwo(Player two) {
        this.two = two;
    }
}
