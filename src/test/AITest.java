package test;

import model.ProtocolMessages;
import model.game.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class AITest {
    private Field[] array;
    private Board board;
    private Player player;
    private Game game;
    private Boolean hit = true;

    @BeforeEach
    public void setUp() {
        array = new Field[Board.HEIGHT * Board.WIDTH];
        for (int i = 0; i < Board.HEIGHT * Board.WIDTH; i++) {
            array[i] = new Field();
        }
        board = new Board(array);
        player = new ComputerPlayer();
        game = new Game(player, new HumanPlayer("test"));
        game.getPlayerTwo().setBoard(board);
        player.setCurrentGame(game);
    }

    @Test
    public void checkRight() {
        array[50] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        Assertions.assertEquals(51, this.player.determineMove());
    }
    @Test
    public void checkLeft() {
        array[50] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        board.getField(51).setHit(hit);
        Assertions.assertEquals(49, this.player.determineMove());
    }

    @Test
    public void checkBottom() {
        array[50] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        board.getField(51).setHit(hit);
        board.getField(49).setHit(hit);
        Assertions.assertEquals(65, this.player.determineMove());
    }

    @Test
    public void checkTop() {
        array[50] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        board.getField(51).setHit(hit);
        board.getField(49).setHit(hit);
        board.getField(65).setHit(hit);
        Assertions.assertEquals(35, this.player.determineMove());
    }

    @Test
    public void overEdge() {
        array[29] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        array[28] = new Field(new Ship(-1, ProtocolMessages.Ship.UNKNOWN));
        board.getField(29).setHit(hit);
        board.getField(28).setHit(hit);
        Assertions.assertEquals(27, this.player.determineMove());
    }

    @Test
    public void destroyed() {
        array[81] = new Field(new Ship(-1, ProtocolMessages.Ship.SUPERPATROL));
        array[82] = new Field(new Ship(-1, ProtocolMessages.Ship.SUPERPATROL));
        Assertions.assertNotEquals(83, this.player.determineMove());
        Assertions.assertNotEquals(80, this.player.determineMove());
        Assertions.assertNotEquals(96, this.player.determineMove());
        Assertions.assertNotEquals(66, this.player.determineMove());
    }
}
