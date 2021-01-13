package model.game;

import model.ProtocolMessages;

import java.util.Random;

public class ComputerPlayer extends Player {
    private String name;
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    public ComputerPlayer() {
        super();
        this.name = "AI player";
    }

    public String getName() {
        return this.name;
    }

    public int determineMove (Board board) {
        for (int i = 0; i < (WIDTH * HEIGHT); i++) {
            int k = i;
            int p = i;
            if (board.getField(i).getShip().getType() == ProtocolMessages.Ship.UNKNOWN) {
                while ((board.getField(k).getShip().getType() == ProtocolMessages.Ship.UNKNOWN)){
                    k++;
                }
                while ((board.getField(p).getShip().getType() == ProtocolMessages.Ship.UNKNOWN)){
                    p = p + WIDTH;
                }
                if (k >= p){
                    if (!((k + 1) % WIDTH == 1)) {return k;}
                    else {return k + WIDTH - 1;}
                }
                else {
                    if (135 < p &&  p < 149 ) {return p - WIDTH + 1;}
                    else { return p;}
                }
            }
        }
        int possible = randomMove();
        while (true) {
        if (!board.getField(possible).isHit()) {
            return possible;
        }
        possible ++;
        }
    }


    public int randomMove () {
        return (int) (Math.random() * 150);
    }
}
