package edu.sustech.game.pane;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static java.lang.Thread.sleep;

public class GameAI extends Pane {
    private CardMatrixPane cardMatrixPane;
    private VBox menu;
    private int time;
    private boolean aiProcessed = false;//键是否已经处理

    public GameAI(CardMatrixPane cardMatrixPane) {
        this.cardMatrixPane = cardMatrixPane;
        int time = 1;

        //创建按钮
        Button OneStepButton = new Button("AI推荐");
        Button AllStepsButton = new Button("电脑托管");

        //调整尺寸
        OneStepButton.setMinSize(80, 80);
        AllStepsButton.setMinSize(80, 80);

        //初始化VBox布局

        VBox verticalButtons = new VBox(10, OneStepButton, AllStepsButton); // 上、下排列
        menu = verticalButtons;


        OneStepButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            cardMatrixPane.goUp();       // Main action logic
            cardMatrixPane.afterAction(); // After action logic
        });


        AllStepsButton.setOnAction(e -> {

            AllStepsButton.setStyle("-fx-background-color: #32c9a1");
            AllStepsButton.setText("STOP");
            cardMatrixPane.requestFocus();
            AnimationTimer timer2 = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (aiProcessed) {
                        return;
                    }
                    aiProcessed = true;
                    cardMatrixPane.beforeAction(); // Before action logic
                    cardMatrixPane.goUp();       // Main action logic
                    cardMatrixPane.afterAction(); // After action logic
                    if (cardMatrixPane.afterAction()) {
                        aiProcessed = false;
                        AllStepsButton.setStyle("-fx-background-color: #ffffff");
                        AllStepsButton.setText("电脑托管");
                        cardMatrixPane.requestFocus();
                        return;
                    }
                    aiProcessed = false;

                }
            };
            timer2.start();
            //再次按键后停止且初始化，
            AllStepsButton.setOnAction(event -> {
                timer2.stop();
                AllStepsButton.setStyle("-fx-background-color: #ffffff");
                AllStepsButton.setText("电脑托管");
            });
            cardMatrixPane.requestFocus();


        });


    }


//        // 通过定时器实现自动游戏
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                cardMatrixPane.beforeAction();
//                cardMatrixPane.goUp();
//                cardMatrixPane.afterAction();
//            }
//        }).start();

    private void AIcontrol() {

    }

    public VBox getLayout() {
        return menu;
    }


}
