package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class Controller {
    @FXML
    private Text huts;
    @FXML
    private Button button;

    public void buttonPressText(ActionEvent press) {
        huts.setText("This is a custom text!");
    }

    public void buttonPressExit(ActionEvent exit) {
        System.exit(0);
    }

    public void buttonPressKaas(ActionEvent press) {
        button.setText("download nou kaasje");
    }
}
