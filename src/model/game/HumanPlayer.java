package model.game;
import model.exceptions.InvalidIndex;

public class HumanPlayer extends Player{

    /**
     * @ensures that a HumanPlayer object is constructed with given name and, if emptyField == true, a Fields array with EMPTY fields
     * @requires a valid String and boolean
     * @param name is a String containing the player name
     * @param emptyFields is a boolean true/false
     */
    public HumanPlayer(String name, boolean emptyFields) {
        super(name, emptyFields);
    }

    /**
     * @ensures that a HumanPlayer object is constructed with given name
     * @requires a valid String
     * @param name is a String containing the player name
     */
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * @ensures that index is returned of not shot at field on opponents board. This could be used if the player does not decide for themselves, for example.
     * @return and int between 0 and 149
     */
    @Override
    public int determineMove() {
        return Board.randomIndexOnFreeField(this.getCurrentGame().getPlayerTwo().getBoard().getFieldsArray());
    }

    /**
     * @ensures that an int is returned with the move of the HumanPlayer
     * @requires a valid command as specified in the protocol
     * @param command is a non-null String
     * @return an int between 0 and 149
     * @throws InvalidIndex is thrown when index does not exist or Field has already been shot at earlier
     */
    public int determineMove(String command) throws InvalidIndex {
        int i = Board.index(command);
        if (i == -1) {
            throw new InvalidIndex("This index does not exist on the board!");
        }
        else if (this.getCurrentGame().getPlayerTwo().getBoard().getField(i).isHit()) {
            throw new InvalidIndex("This index has already been hit!");
        }
        else {
            return i;
        }
    }
}
