package view;

import controller.ViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.ProtocolMessages;
import model.game.Board;
import model.game.Field;
import model.game.Ship;

import java.util.Timer;
import java.util.TimerTask;

public class Game {
    private ViewDelegate controller = ViewController.sharedInstance;

    @FXML private GridPane playerField;
    @FXML private GridPane enemyField;
    @FXML private Text gameTimer;
    @FXML private Text turnTimer;
    @FXML private TextField moveTextField;
    @FXML private Button fireButton;

    public void updatePlayerField(Field field, int index, GridPane pane){
       Rectangle rect = (Rectangle) ((StackPane) pane.getChildren().get(index)).getChildren().get(0);

       switch (field.getShip().getType()) {
           case CARRIER:
               rect.setFill(Color.web("#b6d7a8"));
               break;
           case BATTLESHIP:
               rect.setFill(Color.web("#a4c2f4"));
               break;
           case DESTROYER:
               rect.setFill(Color.web("#ffe599"));
               break;
           case SUPERPATROL:
               rect.setFill(Color.web("#f9cb9c"));
               break;
           case PATROLBOAT:
               rect.setFill(Color.web("#ea9999"));
               break;
           case UNKNOWN:
               rect.setFill(Color.web("#b7b7b7"));
               break;
           default:
               rect.setFill(Color.web("#ffffff"));
               break;
       }

       if (field.isHit()) {
            rect.setStroke(Color.RED);
            rect.setStrokeWidth(2.5);
       }
       else {
           rect.setStroke(Color.WHITE);
       }
    }

    //time in seconds
    public void updateGeneralTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        gameTimer.setText(minutes + ":" + seconds);
    }

    public void startTurnTimer() {
        Timer timer = new Timer();
        final int[] time = {30};
        moveTextField.setEditable(true);
        fireButton.setDisable(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!(time[0] < 0)) {
                    if (time[0] < 10) {
                        turnTimer.setText("0:0" + time[0]);
                    }
                    else {
                        turnTimer.setText("0:" + time[0]);
                    }
                    time[0]--;
                }
                else {
                    stopTurnTimer(timer);
                }
            }
        }, 0, 1000);
    }

    public void stopTurnTimer(Timer timer) {
        timer.cancel();
        fireButton.setDisable(true);
        moveTextField.setEditable(false);
    }

    public void pressFireButton() {
        controller.sendMove(moveTextField.getText());
    }

    public GridPane getPlayerField() {
        return playerField;
    }

    public GridPane getEnemyField() {
        return enemyField;
    }


    public void test() {
        Board board = new Board("0,0,C1,C1,C1,C1,S5,S2,0,P6,0,0,0,0,0,0,P9,0,0,0,0,S5,S2,S1,0,0,D3,D3,D3,0,0,0,0,B1,0,0,0,0,S1,0,B0,P0,D2,D0,0,0,0,P2,B1,C0,0,S3,S3,0,0,B0,0,D2,D0,0,S7,S7,0,B1,C0,P4,0,S6,0,0,B0,0,D2,D0,0,0,0,0,B1,C0,0,0,S6,0,0,B0,0,S0,0,0,0,0,0,D4,C0,0,0,0,0,0,0,0,S0,0,0,0,0,0,D4,C0,B2,B2,B2,B2,0,P7,0,0,0,P3,0,0,0,D4,0,0,0,0,0,P5,0,0,0,0,P8,S4,S4,0,0,D1,D1,D1,0,P1,0,0,0,0,0,0,");
        for (int i = 0; i < 150; i++) {
           updatePlayerField(board.getField(i), i, playerField);
        }
    }
}
