package edu.sustech.game.control;

import edu.sustech.game.pane.CardPane;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import edu.sustech.game.pane.CardMatrixPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


public class GameController extends Pane {

    CardMatrixPane cardMatrixPane;
    public GameController(CardMatrixPane cardMatrixPane){
        this.cardMatrixPane = cardMatrixPane;
    }

//这个类暂时没有用

}
