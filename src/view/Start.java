package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.networking.Client;

import java.io.IOException;

public class Start {
    @FXML private Button hostbig;
    @FXML private Button joinbig;
    @FXML private Button hostbutton;
    @FXML private Button joinbutton;
    @FXML private TextField hostport;
    @FXML private TextField hostname;
    @FXML private TextField joinip;
    @FXML private TextField joinport;
    @FXML private TextField joinname;

    public void bigHostButtonpress(ActionEvent e) {
        if (joinbig.isVisible()) {
            hostport.setVisible(true);
            hostname.setVisible(true);
            hostbutton.setVisible(true);
            joinbig.setVisible(false);
            hostbig.setText("<- Go back");
        }
        else {
            hostport.setVisible(false);
            hostname.setVisible(false);
            hostbutton.setVisible(false);
            joinbig.setVisible(true);
            hostbig.setText("Host game");
        }
    }

    public void bigJoinButtonpress(ActionEvent e) {
        if (hostbig.isVisible()) {
            joinip.setVisible(true);
            joinport.setVisible(true);
            joinname.setVisible(true);
            joinbutton.setVisible(true);
            hostbig.setVisible(false);
            joinbig.setText("<- Go back");
        }
        else {
            joinip.setVisible(false);
            joinport.setVisible(false);
            joinname.setVisible(false);
            joinbutton.setVisible(false);
            hostbig.setVisible(true);
            joinbig.setText("Join game");
        }
    }

    @FXML
    public void switchToLobby(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        if(e.getSource()==joinbutton || e.getSource()==hostbutton){
            if (e.getSource()==joinbutton) {
                stage = (Stage) joinbutton.getScene().getWindow();
            }
            else {
                stage = (Stage) hostbutton.getScene().getWindow();
            }

            root = FXMLLoader.load(getClass().getResource("../view/lobby.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void hostGame(ActionEvent e) {

    }
}
