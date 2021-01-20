package view;

import controller.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.Socket;

public class View extends Application {
    private ViewController controller;

    @Override
    public void init() throws Exception {
        super.init();
        this.controller = ViewController.getInstance();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Font.loadFont(getClass().getResourceAsStream("../resources/roboto.ttf"), 14);

        Parent root = FXMLLoader.load(getClass().getResource("../view/game.fxml"));
        primaryStage.setTitle("Battleships by StjinTjin and R1NS3");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public ViewDelegate getController() {
        return controller;
    }

    public void setController(ViewController controller) {
        this.controller = controller;
    }
}
