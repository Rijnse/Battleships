package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FileInputStream inputstream = new FileInputStream("C:\\Users\\Rinse\\Documents\\GitHub\\Battleships\\Afbeelding1.jpg");
        Image image = new Image(inputstream);
        ImageView imageView = new ImageView(image);

        Button exit = new Button();
        exit.setText("Click me if you dare!");
        exit.setStyle("-fx-border-color: #ff0000; -fx-border-width: 5px;");
        exit.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                System.exit(0);
            }
        });


        Label label = new Label("Dikke vette huts!");
        VBox box = new VBox(20, label, imageView, exit);
        box.setAlignment(Pos.CENTER);

        primaryStage.setTitle("Battleships");
        primaryStage.setScene(new Scene(box, 500, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
