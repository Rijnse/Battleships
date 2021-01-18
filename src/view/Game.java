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

    public void updatePlayerField(Field field, int index){
       Rectangle rect = (Rectangle) ((StackPane) playerField.getChildren().get(0)).getChildren().get(0);

       if (field.isHit()) {
            rect.setStroke(Color.RED);
       }
       else {
           rect.setStroke(Color.WHITESMOKE);
       }


    }

    public void updateEnemyField() {
    }
}
