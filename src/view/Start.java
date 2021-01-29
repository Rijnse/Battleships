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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Start {
    //instance of controller is called for and set
    private ViewController controller = ViewController.getInstance();

    //Attributes of JavaFX on the start screen
    @FXML private Button hostbig;
    @FXML private Button joinbig;
    @FXML private Button computerbig;

    @FXML private Button computerbutton;
    @FXML private Button hostbutton;
    @FXML private Button joinbutton;

    @FXML private TextField hostport;
    @FXML private TextField hostname;
    @FXML private TextField hostip;
    @FXML private TextField joinip;
    @FXML private TextField joinport;
    @FXML private TextField joinname;
    @FXML private TextField computername;

    @FXML private AnchorPane joinanchor;
    @FXML private AnchorPane anchorhost;
    @FXML private AnchorPane anchorcomputer;

    @FXML private AnchorPane popup;
    @FXML private Text popuptitle;
    @FXML private Text popupdesc;

    /**
     * @ensures that important attributes in ViewController are set on opening the game view
     */
    @FXML public void initialize() {
        ViewController.getInstance().setStartScreen(this);
        ViewController.getInstance().currentScreen = "start";
    }

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

    /**
     * @ensures that view is switched to lobby screen
     * @throws IOException if right FXML file can't be found
     */
    @FXML public void switchToLobby() throws IOException {
        Stage stage;
        Parent root;

            stage = (Stage) joinbutton.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("../view/lobby.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    /**
     * @ensures that popup is displayed in Start View. The duration is capped at 3 seconds.
     * @param title is the title of the popup
     * @param desc is the description of the popup
     */
    @FXML public void showPopUp(String title, String desc) {
        Timer timer = new Timer();
        final int[] time = {3};
        popuptitle.setText(title);
        popupdesc.setText(desc);
        popup.setVisible(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!(time[0] < 0)) {
                    time[0]--;
                }
                else {
                    timer.cancel();
                    popup.setVisible(false);
                }
            }
        }, 0, 1000);
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
                try {
                    controller.hostGame(hostip.getText(), port, hostname.getText());
                }
                catch (IOException e) {
                    hostbutton.setText("An error has occurred!");
                }
            }
        }
        catch (NumberFormatException e) {
            hostbutton.setText("Invalid port!");
        }
    }

    /**
     * @requires a valid input of the port and name fields, meaning that these values must != null
     * @ensures that the joinGame method in the controller class is called
     */
    public void joinGame() {
        try {
            if (joinname.getText().length() > 12 || joinname.getText().length() < 1) {
                joinbutton.setText("Name too long/short!");
            }
            else {
                controller.joinGame(joinip.getText(), joinport.getText(), joinname.getText());
            }
        }
        catch (NumberFormatException exception) {
            joinbutton.setText("Invalid port!");
        }
        catch (IOException e) {
            joinbutton.setText("Could not connect!");
        }
    }

    /**
     * @requires a valid name put in in the textfield
     * @ensures that the botGame method in the controller is called
     */
    public void computerGame() {
        if (computername.getText().length() > 12 || computername.getText().length() < 1) {
            computerbutton.setText("Name too long/short!");
        }
        else {
            try {
                controller.botGame(computername.getText());
            } catch (IOException e) {
                computerbutton.setText("Something went wrong!");
            }
        }
    }
}
