package view;

import controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import static model.ProtocolMessages.*;
import model.exceptions.InvalidIndex;
import model.game.Field;
import model.game.HumanPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class Game {
    //instance of controller is called for and set
    private ViewController controller = ViewController.getInstance();

    //Attributes of the Game View, conveniently named after their function
    @FXML private GridPane playerField;
    @FXML private GridPane enemyField;
    @FXML private Text gameTimer;
    @FXML private Text turnTimer;
    @FXML private TextField moveTextField;
    @FXML private Button fireButton;

    @FXML private AnchorPane popup;
    @FXML private Text popuptitle;
    @FXML private Text popupdesc;

    @FXML private Text scoreplayerone;
    @FXML private Text scoreplayertwo;
    @FXML private Text nameplayerone;
    @FXML private Text nameplayertwo;

    //Turn timer used in method
    private Timer timer;

    /**
     * @ensures that important attributes in ViewController are set on opening the game view
     */
    @FXML public void initialize() {
        ViewController.getInstance().setGameView(this);
        ViewController.getInstance().gameIni = true;
        ViewController.getInstance().currentScreen = "game";
    }

    /**
     * @ensures that given index on the player field in the GameView is updated according to the info in the given Field
     * @param field is the Field object which needs to be represented on the board
     * @param index is the index of the given Field
     */
    public void updateOwnField(Field field, int index) {
        updatePlayerField(field, index, playerField);
    }

    /**
     * @ensures that given index on the enemy field in the GameView is updated according to the info in the given Field
     * @param field is the Field object which needs to be represented on the board
     * @param index is the index of the given Field
     */
    public void updateEnemyField(Field field, int index) {
        updatePlayerField(field, index, enemyField);
    }

    /**
     * @ensures *SEE UPDATEOWNFIELD & UPDATEENEMYFIELD*
     * @param field ^
     * @param index ^
     * @param pane is the field to be updated (either Player or Enemy)
     */
    public void updatePlayerField(Field field, int index, GridPane pane){
       Rectangle rect = (Rectangle) ((StackPane) pane.getChildren().get(index)).getChildren().get(0);

       //Colors: CARRIER = GREEN, BATTLESHIP = BLUE, DESTROYER = YELLOW, SUPERPATROL = ORANGE, PATROLBOAT = RED, UNKNOWN SHIP = GREY and EMPTY = WHITE;
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

    /**
     * @ensures that remaining game time is displayed in game view
     * @param time in seconds as an int
     */
    public void updateGeneralTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        if (seconds < 10) {
            gameTimer.setText(minutes + ":0" + seconds);
        }
        else {
            gameTimer.setText(minutes + ":" + seconds);
        }
    }

    /**
     * @ensures that turn of player is started. This means that TextField and Fire button become available and the turn timer starts running.
     */
    public void startTurnTimer() {
        timer = new Timer();
        final int[] time = {TURN_TIME};
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

    /**
     * @ensures that timer is stopped and move fields become unavailable again.
     * @param timer is the Timer object which needs to be canceled.
     */
    public void stopTurnTimer(Timer timer) {
        timer.cancel();
        fireButton.setDisable(true);
        moveTextField.setEditable(false);
    }

    /**
     * @ensures that index is retrieved from textfield and sent to the server by the controller. If index is invalid, an ERROR is shown and the user may try again.
     * @requires the fire button to be pressed.
     */
    public void pressFireButton() {
        HumanPlayer player = (HumanPlayer) ViewController.getInstance().getClient().getPlayer();
        try {
            String input = moveTextField.getText().replaceAll(" ", "");
            int index = player.determineMove(input);
            if (!ViewController.getInstance().getClient().getPlayer().getCurrentGame().getPlayerTwo().getBoard().getFieldsArray()[index].isHit()) {
                controller.sendMove(input);
                stopTurnTimer(timer);
            }
        } catch (InvalidIndex invalidIndex) {
            showPopUp("ERROR!", invalidIndex.getMessage(), 3);
        }
    }

    /**
     * @ensures that popup is shown in game view
     * @param title is the title of the popup
     * @param desc is the description of the popup
     * @param popuptime is the duration in seconds for which the popup should remain on the screen
     */
    public void showPopUp(String title, String desc, int popuptime) {
        Timer timer = new Timer();
        final int[] time = {popuptime};
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
     * @ensures that names of players are updated in Game View
     * @param one != null
     * @param two != null
     */
    public void updatePlayerNames(String one, String two) {
        nameplayerone.setText(one);
        nameplayertwo.setText(two);
    }

    /**
     * @ensures that scores of players are updated in Game View
     * @param one != null
     * @param two != null
     */
    public void updatePlayerScores(int one, int two) {
        scoreplayerone.setText(String.valueOf(one));
        scoreplayertwo.setText(String.valueOf(two));
    }
}
