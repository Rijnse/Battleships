package model.game;

import model.ProtocolMessages;
import static model.ProtocolMessages.*;

public class ComputerPlayer extends Player {
    public static final int WIDTH = BOARD_DIMENSIONS[1];
    public static final int HEIGHT = BOARD_DIMENSIONS[0];

    /**
     * @ensures that a ComputerPlayer is constructed with the Player constructor and pre-defined name (We figured Admiral AI was funny)
     * @requires the Player constructor to be fully functional
     */
    public ComputerPlayer() {
        super("AdmiralAI");
    }

    /**
     * @ensures that an int is returned with an index which is the best move according to the AI
     * @requires that the board object of the Player's opponent is properly defined
     * @return an int between 0 and 149
     */
    @Override
    public int determineMove () {
        Board opponentBoard = this.getCurrentGame().getPlayerTwo().getBoard();
        for (int i = 0; i < (WIDTH * HEIGHT); i++) {
            // search for a ship that has not been destroyed yet.
            if (opponentBoard.getField(i).getShip().getType() == ProtocolMessages.Ship.UNKNOWN) {
                int k = i;
                int l = i;
                int p = i;
                int m = i;
                // look if there are more unknown ships to the right.
                while (opponentBoard.getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && (k + 1) % WIDTH != 0 && k < (WIDTH * HEIGHT) - 1) {
                    k++;
                }
                // look if there are more unknown ships to the left.
                while (opponentBoard.getField(l).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && (l - 1) % WIDTH != (WIDTH - 1) && l > 0) {
                    l--;
                }
                // look if there are more unknown ships on the bottom.
                while (opponentBoard.getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && p < (WIDTH * (HEIGHT - 1))) {
                    p = p + WIDTH;
                }
                // look if there are more unknown ships on the top.
                while (opponentBoard.getField(m).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && m > (WIDTH - 1)) {
                    m = m - WIDTH;
                }
                int rPlaces = k - i - 1;
                int lPlaces = i - l - 1;
                int bPlaces = ((p - i) / WIDTH) - 1;
                int tPlaces = ((i - m) / WIDTH) - 1;

                // check if there is an other unknown ship next to the unknown ship at index i.
                if (rPlaces != 0 || lPlaces != 0 || bPlaces != 0 || tPlaces != 0) {
                    if (rPlaces >= lPlaces && rPlaces >= bPlaces && rPlaces >= tPlaces && !opponentBoard.getField(k).isHit()) {
                        return k;
                    }
                    else if (lPlaces > rPlaces && lPlaces >= bPlaces && lPlaces >= tPlaces && !opponentBoard.getField(l).isHit()) {
                        return l;
                    }
                    else if (bPlaces > rPlaces && bPlaces > lPlaces && bPlaces >= tPlaces  && !opponentBoard.getField(p).isHit()) {
                        return p;
                    }
                    else if (tPlaces > rPlaces && tPlaces > lPlaces && tPlaces > bPlaces && !opponentBoard.getField(m).isHit()) {
                        return m;
                    }
                }
                else {
                    if (!opponentBoard.getField(k).isHit()) {
                        return k;
                    }
                    else if (!opponentBoard.getField(l).isHit()) {
                        return l;
                    }
                    else if (!opponentBoard.getField(p).isHit()) {
                        return p;
                    }
                    else if (!opponentBoard.getField(m).isHit()) {
                        return m;
                    }
                }
            }
        }
        // if there are no unknown ships return a random index
        return randomMove(opponentBoard.getFieldsArray());
    }


    /**
     * @ensures that a random int between 0 and 149 is generated
     * @return an int
     */
    public static int randomMove (Field[] array) {
        int number = (int) (Math.random() * WIDTH * HEIGHT);
        if (!array[number].isHit()) {
            return number;
        }
        else {
            return randomMove(array);
        }
    }
}
