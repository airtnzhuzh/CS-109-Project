package edu.sustech.game.pane;

import edu.sustech.game.app.Game;
import edu.sustech.game.control.GameAI;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Optional;


public class GameAIMenu extends Pane {
    private CardMatrixPane cardMatrixPane;
    private VBox menu;
    private int time;
    private boolean aiProcessed = false;//键是否已经处理
    private Button AllStepsButton;
    private Button OneStepButton;
    private GameAI gameAI;
    private ToolMenu toolMenu;


    // instance variable
    private long previousTimestamp ;
    private final long nanoSecsPerFrame = Math.round(1.0/10 * 1e9);

    public GameAIMenu(CardMatrixPane cardMatrixPane) {
        this.cardMatrixPane = cardMatrixPane;
        gameAI = new GameAI();
        int time = 1;

        //创建按钮
        OneStepButton = new Button("AI推荐");
        AllStepsButton = new Button("电脑托管");
        //Tool按钮
        toolMenu = new ToolMenu(cardMatrixPane);


        //调整尺寸
        OneStepButton.setMinSize(80, 80);
        AllStepsButton.setMinSize(80, 80);

        //初始化VBox布局

        VBox verticalButtons = new VBox(10, OneStepButton, AllStepsButton,toolMenu.getLayout()); // 上、下排列
        menu = verticalButtons;


        OneStepButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            gameAI.move(cardMatrixPane);       // Main action logic
            if(cardMatrixPane.isGameOver()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setHeaderText(null);
                alert.setContentText("游戏结束，本次最大数字为 " + cardMatrixPane.getMaxCard().getNumber() + "，是否重新开始？");

                ButtonType buttonTypeYes = new ButtonType("是");
                ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeYes) {
                    cardMatrixPane.restartMatrix();
                } else {
                    // Close the alert
                    alert.close();
                }
            } // After action logic
        });


        AllStepsButton.setOnAction(e -> {
            AllStepsButton.setStyle("-fx-background-color: #32c9a1");
            AllStepsButton.setText("STOP");
            AllStepsButton();
        });
    }




    private void AllStepsButton() {

        cardMatrixPane.requestFocus();
        AnimationTimer timer2 = new AnimationTimer() {

            //设置刷新频率


            @Override
            public void handle(long now) {

                if (aiProcessed) {
                    System.out.println("test02");
                    aiProcessed = true;
                    return;
                }
                aiProcessed = true;
                System.out.println(111);
                gameAI.move(cardMatrixPane);       // Main action logic
                System.out.println(222);
                if(cardMatrixPane.isGameOver()) {// After action logic
                    System.out.println("test03");
                    AllStepsButton.setStyle("-fx-background-color: #ffffff");
                    AllStepsButton.setText("电脑托管");
                    this.stop();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(alert.getAlertType().toString());
                    alert.setHeaderText(null);
                    alert.setContentText("游戏结束，本次最大数字为 " + cardMatrixPane.getMaxCard().getNumber() + "，是否重新开始？");

                    ButtonType buttonTypeYes = new ButtonType("是");
                    ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == buttonTypeYes) {
                        cardMatrixPane.restartMatrix();
                    } else {
                        // Close the alert
                        alert.close();
                    }
                }

                aiProcessed = false;

            }

        };

        timer2.start();


        //再次按键后停止且初始化，
        AllStepsButton.setOnAction(event -> {
            timer2.stop();
            //event绑定回去
            AllStepsButton.setStyle("-fx-background-color: #ffffff");
            AllStepsButton.setText("电脑托管");
            cardMatrixPane.requestFocus();
            AllStepsButton.setOnAction(e -> {
                AllStepsButton.setStyle("-fx-background-color: #32c9a1");
                AllStepsButton.setText("STOP");
                AllStepsButton();
            });
        });
    }


    public VBox getLayout() {
        return menu;
    }


}
