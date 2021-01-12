package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class Lobby {
    @FXML private Button leavebutton;

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

    @FXML private Button balenciaga;
    MediaPlayer mediaPlayer;
    @FXML
    public void audioPlayerButton(ActionEvent e) throws MalformedURLException {
        if (e.getSource() == balenciaga) {
            Media hit = new Media(new File("src/resources/music.mp3").toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        }
    }
}
