package test;

import model.ProtocolMessages;
import model.game.Board;
import model.game.Ship;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardMakerTest {
    private Board board;
    @BeforeEach
    public void setup () {
        board = new Board();
    }

    @Test
    public void checkEnoughShips () {
        int correctShipCounter = 0;
        for (Ship s : board.SHIPS) {
            for (int i = 0; i < 150; i ++) {
                if (board.getField(i).getShip().equals(s)) {
                    correctShipCounter ++;
                    break;
                }
            }
        }
        Assertions.assertEquals(correctShipCounter, 28);
    }

    @Test
    public void checkNoShipsOverBoundary() {
        for (int i = 14; i < 135; i = i + 15) {
            if (!board.getField(i).getShip().getType().equals(ProtocolMessages.Ship.EMPTY) && !board.getField(i + 1).getShip().getType().equals(ProtocolMessages.Ship.EMPTY)) {
                Assertions.assertNotEquals(board.getField(i).getShip(), board.getField(i + 1).getShip());
            }
        }
    }
}
