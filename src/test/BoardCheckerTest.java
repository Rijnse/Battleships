package test;
import model.ProtocolMessages;
import model.game.Board;
import model.game.Field;
import model.game.Ship;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardCheckerTest {
    private Board board;
    @BeforeEach
    public void setup () {
        board = new Board();
    }

    @Test
    public void checkValidBoard () {
        Assertions.assertTrue(board.checkValidBoard());
        Board board1 = board;
        for (int i = 0; i < 150; i ++) {
            if (board1.getField(i).getShip().getType().equals(ProtocolMessages.Ship.CARRIER)) {
                board1.getFieldsArray()[i] = new Field();
                break;
            }
        }
        Assertions.assertFalse(board1.checkValidBoard());
        Board board2 = board;
        for (int i = 0; i < 150; i ++) {
            if (board2.getField(i).getShip().getType().equals(ProtocolMessages.Ship.DESTROYER)) {
                board2.getFieldsArray()[i] = new Field();
            }
        }
        Assertions.assertFalse(board2.checkValidBoard());
        Board board3 = board;
        for (int i = 135; i < 150; i ++) {
            if (!board3.getField(i).getShip().getType().equals(ProtocolMessages.Ship.EMPTY)) {
                board3.getFieldsArray()[i] = new Field();
            }
        }
        Assertions.assertFalse(board3.checkValidBoard());
        Board board4 = board;
        for (int i = 0; i < 150; i ++) {
            if (board4.getField(i).getShip().toString().equals("SUPERPATROL1")) {
                board4.getFieldsArray()[1] = new Field();
            }
        }
        Ship ship = new Ship(1, ProtocolMessages.Ship.SUPERPATROL);
        for (int i = 14; i < 135; i = i + 15) {
            if (board4.getField(i).getShip().getType().equals(ProtocolMessages.Ship.EMPTY) && board4.getField(i + 1).getShip().getType().equals(ProtocolMessages.Ship.EMPTY)) {
                board4.getFieldsArray()[i] = new Field(ship);
                board4.getFieldsArray()[i + 1] = new Field(ship);
            }
        }
        Assertions.assertFalse(board4.checkValidBoard());
    }
}
