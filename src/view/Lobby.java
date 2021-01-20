package view;

import controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Lobby {
    @FXML private Button leavebutton;
    @FXML private Text ipaddress;
    @FXML private Text port;
    @FXML private Text playerOneName;
    @FXML private Text playerTwoName;

    @FXML private Button balenciaga;
    @FXML private Button mutebutton;
    MediaPlayer mediaPlayer;

    private ViewDelegate controller = ViewController.sharedInstance;

    @FXML
    public void leaveGame(ActionEvent e) throws IOException {
        Stage stage;
        Parent root;

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
}
