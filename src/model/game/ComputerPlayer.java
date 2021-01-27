package model.game;

import model.ProtocolMessages;

import java.util.Random;

public class ComputerPlayer extends Player {
    private String name;
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    /**
     * @ensures that a ComputerPlayer is constructed with the Player constructor and pre-defined name (We figured Admiral AI was funny)
     * @requires the Player constructor to be fully functional
     */
    public ComputerPlayer() {
        super("Admiral AI");
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
                int p = i;
                // look if there are more unknown ships to the right.
                while (opponentBoard.getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && (k + 1) % WIDTH != 0 && (k - 1) < (WIDTH * HEIGHT)) {
                    k++;
                }
                // look if there are more unknown ships to the left.
                while (opponentBoard.getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && p < (WIDTH * (HEIGHT - 1))) {
                    p = p + WIDTH;
                }
                int kPlaces = (k - i) - 1;
                int pPlaces = ((p - i) / WIDTH) - 1;

                // check if there is an other unknown ship next to the unknown ship at index i.
                if (kPlaces != 0 || pPlaces != 0) {
                    // return index k if there are more unknown ships to the right than on the bottom and if k is not on the first column of the board.
                    if (kPlaces >= pPlaces) {
                        if ((k + 1) % WIDTH != 0) {
                            return k;
                        }
                    }
                    // return index p if there are more unknown ships on the bottom than to the right and if p is not in the last row of the board.
                    if (pPlaces > kPlaces) {
                        if (p < (WIDTH * (HEIGHT - 1))) {
                            return p;
                        }
                    }
                    // if there is at least one unknown ship to the right of i and k is not on the first column of the board, return k.
                    if (kPlaces != 0) {
                        if ((k + 1) % WIDTH != 0) {
                            return k;
                        }
                    }
                    // if there is at least one unknown ship on the bottom of i and p is not in the last row of the board, return p.
                    if (pPlaces != 0) {
                        if (p < (WIDTH * (HEIGHT - 1))) {
                            return p;
                        }
                    }
                }
            }
        }
        // if there are no unknown ships return a random index
        int possible = randomMove();
        while (possible < (WIDTH * HEIGHT)) {
            if (!opponentBoard.getField(possible).isHit()) {
                return possible;
            }
            if (possible == (WIDTH * HEIGHT) - 1) {
                possible = 0;
            }
            else {
                possible++;
            }
        }
        return -1;
    }

    /**
     * @ensures that a random int between 0 and 149 is generated
     * @return an int
     */
    public static int randomMove () {
        return (int) (Math.random() * WIDTH * HEIGHT);
    }
}
