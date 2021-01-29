package view;

import controller.ViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static model.ProtocolMessages.*;

public class View extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("../resources/roboto.ttf"), 14);

        Parent root = FXMLLoader.load(getClass().getResource("../view/start.fxml"));
        primaryStage.setTitle("Battleships by Stijn Odink and Rinse Dijkstra");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        //Shuts down whole program if window is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (!ViewController.getInstance().currentScreen.equals("start")) {
                    ViewController.getInstance().getClient().sendMessage(EXIT + CS + ViewController.getInstance().getClient().getPlayer().getName());
                }
                System.out.println("Closing program...");
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
