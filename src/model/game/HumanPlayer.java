package model.game;
import model.exceptions.InvalidIndex;

public class HumanPlayer extends Player{
    public HumanPlayer(String name) {
        super(name);
    }

    //fired if player runs out of time, using the static method in Board which always returns a spot which has not been fired at yet!
    @Override
    public int determineMove() {
        return Board.randomIndexOnFreeField(this.getCurrentGame().getPlayerTwo().getBoard().getFieldsArray());
    }

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
