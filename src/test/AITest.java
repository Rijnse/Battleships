package test;

import model.game.Board;
import model.game.Field;
import org.junit.jupiter.api.BeforeEach;

public class AITest {

    @BeforeEach
    public void setUp () {
        Field[] array = new Field[Board.HEIGHT * Board.WIDTH];
        for (int i = 0; i < Board.HEIGHT * Board.WIDTH; i++) {
            array[i] = new Field();
        }
    }

}
