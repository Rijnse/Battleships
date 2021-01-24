package model.game;

import model.ProtocolMessages;

import java.util.Random;

public class ComputerPlayer extends Player {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    public ComputerPlayer() {
        super("Admiral AI");
    }

    @Override
    public int determineMove () {
        for (int i = 0; i < (WIDTH * HEIGHT); i++) {
            int k = i;
            int p = i;
            if (this.getBoard().getField(i).getShip().getType() == ProtocolMessages.Ship.UNKNOWN) {
                while ((this.getBoard().getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN)) {
                    k++;
                }
                while ((this.getBoard().getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN)) {
                    p = p + WIDTH;
                }
                if (k >= p && !this.getBoard().getField(k).isHit()) {
                    if (!((k + 1) % WIDTH == 1)) {
                        return k;
                    }
                    else {
                        return k + WIDTH - 1;
                    }
                }
                else {
                    if (135 < p && p < 149) {
                        return p - WIDTH + 1;
                    }
                    else {
                        return p;
                    }
                }
            }
        }
        int possible = randomMove();
        while (true) {
            if (!this.getBoard().getField(possible).isHit()) {
                return possible;
            }
            possible ++;
        }
    }

    public int randomMove () {
        return (int) (Math.random() * 150);
    }
}
