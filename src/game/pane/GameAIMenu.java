package edu.sustech.game.pane;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static java.lang.Thread.sleep;

public class GameAIMenu extends Pane {
    private CardMatrixPane cardMatrixPane;
    private VBox menu;
    private int time;
    private boolean aiProcessed = false;//键是否已经处理
    private Button AllStepsButton;
    private Button OneStepButton;

    // instance variable
    private long previousTimestamp ;
    private long nanoSecsPerFrame = Math.round(1.0/10 * 1e9);

    public GameAIMenu(CardMatrixPane cardMatrixPane) {
        this.cardMatrixPane = cardMatrixPane;
        int time = 1;

        //创建按钮
        OneStepButton = new Button("AI推荐");
        AllStepsButton = new Button("电脑托管");

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
            AllStepsButton();
        });
    }




    private void AllStepsButton() {

        cardMatrixPane.requestFocus();
        AnimationTimer timer2 = new AnimationTimer() {

            //设置刷新频率


            @Override
            public void handle(long timeStamp) {
                if (timeStamp - previousTimestamp < nanoSecsPerFrame)
                {
                    return;
                }
                previousTimestamp = timeStamp;
                if (aiProcessed) {
                    return;
                }
//                if(!cardMatrixPane.isNotGameOver()){
//                    AllStepsButton.setStyle("-fx-background-color: #ffffff");
//                    AllStepsButton.setText("电脑托管");
//                    cardMatrixPane.requestFocus();
//                    cardMatrixPane.afterAction();
//                    this.stop();
//                }
                aiProcessed = true;
                cardMatrixPane.beforeAction(); // Before action logic
                cardMatrixPane.goUp();       // Main action logic
                if(!cardMatrixPane.isNotGameOver()) {// After action logic
                    AllStepsButton.setStyle("-fx-background-color: #ffffff");
                    AllStepsButton.setText("电脑托管");
                    cardMatrixPane.afterAction();
                    this.stop();
                }
                else {
                    cardMatrixPane.afterAction();
                }
                aiProcessed = false;

            }

        };

        timer2.start();
        if (!cardMatrixPane.isNotGameOver()) {
            timer2.stop();
        }

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
