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
            if (opponentBoard.getField(i).getShip().getType() == ProtocolMessages.Ship.UNKNOWN) {
                int k = i;
                int p = i;
                while (opponentBoard.getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && (k + 1) % WIDTH != 0 && (k - 1) < (WIDTH * HEIGHT)) {
                    k++;
                }
                while (opponentBoard.getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && p < (WIDTH * (HEIGHT - 1))) {
                    p = p + WIDTH;
                }
                int kPlaces = k - i;
                int pPlaces = (p - i) / WIDTH;

                if (kPlaces != 0 || pPlaces != 0) {
                    if (kPlaces >= pPlaces) {
                        if ((k + 1) % WIDTH != 0) {
                            return k;
                        }
                    }
                    if (pPlaces > kPlaces) {
                        if (p < (WIDTH * (HEIGHT - 1))) {
                            return p;
                        }
                    }
                    if (kPlaces != 0) {
                        if ((k + 1) % WIDTH != 0) {
                            return k;
                        }
                    }
                    if (pPlaces != 0) {
                        if (p < (WIDTH * (HEIGHT - 1))) {
                            return p;
                        }
                    }
                }
            }
        }
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
