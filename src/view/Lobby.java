package view;

import controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

public class Lobby {
    //instance of controller is called for and set
    private ViewController controller = ViewController.getInstance();

    //All JavaFX attributes used in the lobby
    @FXML public Button leavebutton;
    @FXML public Text ipaddress;
    @FXML public Text port;
    @FXML public Text playerOneName;
    @FXML public Text playerTwoName;

    @FXML private Button startmusic;
    @FXML private Button mutebutton;
    MediaPlayer mediaPlayer;

    @FXML private AnchorPane popup;
    @FXML private Text popuptitle;
    @FXML private Text popupdesc;

    /**
     * @ensures that important attributes in ViewController are set on opening the game view
     */
    @FXML public void initialize() {
        controller.setLobby(this);
        controller.currentScreen = "lobby";
    }

    /**
     * @ensures that the controller is ordered to leave game and that the view switches to the start screen
     */
    @FXML public void leaveGame() {
        try {
            controller.leaveGame();
            switchToStart();
        }
        catch (IOException e) {
            leavebutton.setText("Could not leave game!");
        }
    }

    /**
     * @ensures that information on lobby is updated with provided arguments
     * @requires valid Strings and int
     * @param ip is the ip address of the connected server
     * @param portint is the port of the connected server
     * @param onename is the name of the first player
     * @param twoname is the name of the second player
     */
    @FXML public void updateLobbyInfo(String ip, int portint, String onename, String twoname) {
        ipaddress.setText(ip);
        port.setText(String.valueOf(portint));
        playerOneName.setText(onename);
        playerTwoName.setText(twoname);
    }

    /**
     * @ensures that popup is displayed in Lobby View. The duration is capped at 3 seconds.
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

    @FXML public void pressStartButton() {
        controller.pressStartButton();
    }

    /**
     * @ensures that background music is started in the lobby screen.
     * @requires that the path of the music file is correct
     * @param e is the actionevent which fired the method
     * @throws MalformedURLException if URL of music file does not work
     */
    @FXML public void audioPlayerButton(ActionEvent e) throws MalformedURLException {
        if (e.getSource() == startmusic) {
                Media hit = new Media(new File("src/resources/music.mp3").toURI().toURL().toString());
                mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.setVolume(0.5);
                mediaPlayer.play();
        }
    }

    /**
     * @ensures that the music is muted/unmuted when button is pressed
     * @param e is the actionevent triggering the method
     */
    @FXML public void muteMusic(ActionEvent e) {
        if (e.getSource() == mutebutton) {
            if (mediaPlayer.isMute()) {
                mediaPlayer.setMute(false);
            } else {
                mediaPlayer.setMute(true);
            }
        }
    }

    /**
     * @ensures that view is switched to game screen
     * @throws IOException if right FXML file can't be found
     */
    @FXML public void switchToGameScreen() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) leavebutton.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @ensures that view is switched to start screen
     * @throws IOException if right FXML file can't be found
     */
    @FXML public void switchToStart() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) leavebutton.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../view/start.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
