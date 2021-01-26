package model.game;

public class Game {
    private Player one;
    private Player two;

    /**
     * @ensures that a Game object is constructed with two given players
     * @requires valid Player objects
     * @param one is a Player object of player one
     * @param two is a Player object of player two
     */
    public Game(Player one, Player two) {
        this.one = one;
        this.two = two;
    }

    /**
     * @ensures that a Game object is constructed with given player and one placeholder Player object. The placeholder is used to easily display an empty place in the lobby UI
     * @requires valid Player object
     * @param one is a Player object of player one
     */
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
