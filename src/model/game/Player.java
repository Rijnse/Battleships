package model.game;

import model.ProtocolMessages;

public abstract class Player {
    private int score;
    private Board board;
    private String name;
    private Game currentGame = null;

    public boolean newBoardSet = false; //only needed for serverside operations
    /**
     * @ensures that a Player object (either computer or human) is created with score 0, a random board and the player name
     * @requires the Board() constructor to be fully functional
     * @param name is a String containing the name of the player
     */
    public Player(String name) {
        this.score = 0;
        this.board = new Board();
        this.name = name;
    }

    /**
     * @ensures that a Player object (either computer or human) is created with score 0, a board with empty fields (as opposed to random) and the player name
     * @requires the Board(Field[]) constructor to be fully functional
     * @param name is a String containing the name of the player
     * @param emptyFields is a boolean telling the constructor if the players Board should only contain empty fields
     * This is used when the opponents board is generated on clientside
     */
    public Player(String name, boolean emptyFields) {
        this.score = 0;
        if (emptyFields) {
            Field[] array = new Field[ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]];
            for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
                array[i] = new Field();
            }
            this.board = new Board(array);
        }
        else {
            this.board = new Board();
        }
        this.name = name;
    }

    /**
     * @ensures that the score integer of the player is returned
     * @return an int >= 0
     */
    public int getScore() {
        return this.score;
    }

    /**
     * @ensures a simple method altering the player score by the given amount (e.g. score: 5 and increment = 1. Final score will be 6)w
     * @requires a valid int (in practice > 0, although it will work with negative numbers as well)
     * @param increment is the number with how much the player score should be altered
     */
    public void incrementScore(int increment) {
        this.score = this.score + increment;
    }

    /**
     * @ensures that the Board object property of the Player object is returned
     * @return a Board object
     */
    public Board getBoard() {
        return this.board;
    }


    /**
     * @ensures that board property of player is set to given Board object. Used by server to append boards to serverside player objects.
     * @param board is a Board object
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * @ensures that an index is given at which the player shoots, whether that's a computer or human based player.
     * @return an int between 0 and 149
     */
    public abstract int determineMove();

    /**
     * @ensures that the name of the player is returned
     * @return a String
     */
    public String getName() {
        return name;
    }

    /**
     * @ensures that the name property of the Player object is set to the given String
     * @requires a valid non-null String
     * @param name is the String to which the name should be changed
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @ensures that the Game object of the player is returned
     * @return null or Game object, depending on whether the player is already participating in a match
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * @ensures that the Game property of the Player object is set to the given Game object
     * @param currentGame is a valid Game object
     */
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }
}
