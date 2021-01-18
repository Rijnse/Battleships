package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import model.game.Field;
import model.game.Ship;

public class Game {

    @FXML private GridPane playerField;
    @FXML private GridPane enemyField;

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
// Field field, int index
    public void updatePlayerField(){
        Field field = new Field(new Ship(1, ProtocolMessages.Ship.CARRIER));
        field.setHit(true);
       Rectangle rect = (Rectangle) ((StackPane) playerField.getChildren().get(0)).getChildren().get(0);

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

    public void updateEnemyField() {
    }
}
