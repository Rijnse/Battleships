package view;

import controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Start {
    private ViewDelegate controller = ViewController.sharedInstance;

    @FXML private Button hostbig;
    @FXML private Button joinbig;
    @FXML private Button computerbig;

    @FXML private Button computerbutton;
    @FXML private Button hostbutton;
    @FXML private Button joinbutton;

    @FXML private TextField hostport;
    @FXML private TextField hostname;
    @FXML private TextField joinip;
    @FXML private TextField joinport;
    @FXML private TextField joinname;
    @FXML private TextField computername;

    @FXML private AnchorPane joinanchor;
    @FXML private AnchorPane anchorhost;
    @FXML private AnchorPane anchorcomputer;

    public void bigHostButtonPress(ActionEvent e) {
        if (joinbig.isVisible()) {
            anchorhost.setVisible(true);
            joinbig.setVisible(false);
            computerbig.setVisible(false);
            hostbig.setText("<- Go back");
        }
        else {
            anchorhost.setVisible(false);
            joinbig.setVisible(true);
            computerbig.setVisible(true);
            hostbig.setText("Host game");
        }
    }

    public void bigJoinButtonPress(ActionEvent e) {
        if (hostbig.isVisible()) {
            joinanchor.setVisible(true);
            hostbig.setVisible(false);
            computerbig.setVisible(false);
            joinbig.setText("<- Go back");
        }
        else {
            joinanchor.setVisible(false);
            hostbig.setVisible(true);
            computerbig.setVisible(true);
            joinbig.setText("Join game");
        }
    }

    public void bigComputerButtonPress() {
        if (hostbig.isVisible()) {
            anchorcomputer.setVisible(true);
            hostbig.setVisible(false);
            joinbig.setVisible(false);
            computerbig.setText("<- Go back");
        }
        else {
            anchorcomputer.setVisible(false);
            hostbig.setVisible(true);
            joinbig.setVisible(true);
            computerbig.setText("Play against computer!");
        }
    }

    @FXML
    public void switchToLobby() throws IOException {
        Stage stage;
        Parent root;

            stage = (Stage) joinbutton.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("../view/lobby.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    public void switchToGameScreen() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) joinbutton.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @requires a valid input of the port and name fields, meaning that these values must != null
     * @ensures that the hostGame method in the controller class is called
     */
    @FXML
    public void hostGame() {
        try {
            int port = Integer.parseInt(hostport.getText());
            if (hostname.getText().length() > 12 || hostname.getText().length() < 1) {
                hostbutton.setText("Name too long/short!");
            }
            else {
                controller.hostGame(port, hostname.getText());
                switchToLobby();
            }
        }
        catch (NumberFormatException e) {
            hostbutton.setText("Invalid port!");
        }
        catch (IOException e) {
            hostbutton.setText("Something went wrong!");
        }
    }


    /**
     * @requires a valid input of the port and name fields, meaning that these values must != null
     * @ensures that the joinGame method in the controller class is called
     */
    public void joinGame() {
        try {
            int port = Integer.parseInt(joinport.getText());
            if (joinname.getText().length() > 12 || joinname.getText().length() < 1) {
                joinbutton.setText("Name too long/short!");
            }
            else {
                controller.joinGame(joinip.getText(),port, joinname.getText());
                switchToLobby();
            }
        }
        catch (NumberFormatException exception) {
            joinbutton.setText("Invalid port!");
        }
        catch (IOException e) {
            joinbutton.setText("Something went wrong!");
        }
    }

    public void computerGame() {
        if (computername.getText().length() > 12 || computername.getText().length() < 1) {
            computerbutton.setText("Name too long/short!");
        }
        else {
            controller.botGame(computername.getText());
            try {
                switchToGameScreen();
            } catch (IOException e) {
                computerbutton.setText("Something went wrong!");
            }
        }
    }


}
