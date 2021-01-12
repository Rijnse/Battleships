package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class Game {
    @FXML private Text game;

    public void changeGame(ActionEvent e){
        game.setText("EPIC GAMER MOMENT!");
    }
}
