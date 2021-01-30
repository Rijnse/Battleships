package test;

import model.ProtocolMessages;
import model.game.Board;
import model.game.Ship;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.game.Board.HEIGHT;
import static model.game.Board.WIDTH;

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
            for (int i = 0; i < (WIDTH * HEIGHT ); i ++) {
                if (board.getField(i).getShip().equals(s)) {
                    correctShipCounter ++;
                    break;
                }
            }
        }
        Assertions.assertEquals(correctShipCounter, board.SHIPS.length);
    }

    @Test
    public void checkNoShipsOverBoundary() {
        for (int i = (WIDTH - 1); i < (WIDTH * (HEIGHT - 1)); i = i + WIDTH) {
            if (!board.getField(i).getShip().getType().equals(ProtocolMessages.Ship.EMPTY) && !board.getField(i + 1).getShip().getType().equals(ProtocolMessages.Ship.EMPTY)) {
                Assertions.assertNotEquals(board.getField(i).getShip(), board.getField(i + 1).getShip());
            }
        }
    }

    @Test
    public void checkValidBoard() {
        Assertions.assertTrue(board.checkValidBoard());
    }
}
