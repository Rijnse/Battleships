package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
}
