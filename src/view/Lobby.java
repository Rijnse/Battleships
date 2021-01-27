package view;

import controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
    @FXML public Button leavebutton;
    @FXML public Text ipaddress;
    @FXML public Text port;
    @FXML public Text playerOneName;
    @FXML public Text playerTwoName;

    @FXML private Button balenciaga;
    @FXML private Button mutebutton;
    MediaPlayer mediaPlayer;

    @FXML private AnchorPane popup;
    @FXML private Text popuptitle;
    @FXML private Text popupdesc;

    @FXML
    public void initialize() {
        ViewController.getInstance().setLobby(this);
    }

    @FXML
    public void leaveGame(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

        ViewController.getInstance().leaveGame();

        if(e.getSource()==leavebutton){
            stage = (Stage) leavebutton.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("../view/start.fxml"));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML public void updateLobbyInfo(String ip, int portint, String onename, String twoname) {
        ipaddress.setText(ip);
        port.setText(String.valueOf(portint));
        playerOneName.setText(onename);
        playerTwoName.setText(twoname);
    }

    @FXML
    public void showPopUp(String title, String desc) {
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
        ViewController.getInstance().pressStartButton();
    }


    @FXML
    public void audioPlayerButton(ActionEvent e) throws MalformedURLException {
        if (e.getSource() == balenciaga) {
                Media hit = new Media(new File("src/resources/music.mp3").toURI().toURL().toString());
                mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.setVolume(0.5);
                mediaPlayer.play();
        }
    }

    @FXML
    public void muteMusic(ActionEvent e) {
        if (e.getSource() == mutebutton) {
            if (mediaPlayer.isMute()) {
                mediaPlayer.setMute(false);
            } else {
                mediaPlayer.setMute(true);
            }
        }
    }

    @FXML
    public void switchToGameScreen() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) leavebutton.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToStart() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage) leavebutton.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../view/start.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
