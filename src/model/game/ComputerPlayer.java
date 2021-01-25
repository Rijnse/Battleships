package model.game;

import model.ProtocolMessages;

import java.util.Random;

public class ComputerPlayer extends Player {
    private String name;
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    public ComputerPlayer() {
        super("Admiral AI");
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int determineMove () {
        for (int i = 0; i < (WIDTH * HEIGHT); i++) {
            if (this.getBoard().getField(i).getShip().getType() == ProtocolMessages.Ship.UNKNOWN) {
                int k = i;
                int p = i;
                while (this.getBoard().getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && (k + 1) % WIDTH != 0 && (k - 1) < (WIDTH * HEIGHT)) {
                    k++;
                }
                while (this.getBoard().getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN && p < (WIDTH * (HEIGHT - 1))) {
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
            if (!this.getBoard().getField(possible).isHit()) {
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

    public static int randomMove () {
        return (int) (Math.random() * WIDTH * HEIGHT);
    }
}
