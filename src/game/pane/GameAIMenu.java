package edu.sustech.game.pane;

import edu.sustech.game.app.Game;
import edu.sustech.game.control.GameAI;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

    private Button soundButton;
    boolean soundEnabled = false;


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
        OneStepButton.setStyle("-fx-background-color: #BFD6F7;-fx-font-size:15");
        AllStepsButton.setStyle("-fx-background-color: #A8C0E4;-fx-font-size:15");
        //Tool按钮
        toolMenu = new ToolMenu(cardMatrixPane);

        soundButton = new Button("音效关闭");
        soundButton.setStyle("-fx-background-color: #404f6c;-fx-font-size:15");

        // 添加阴影效果
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.GRAY);
        soundButton.setEffect(dropShadow);
        AllStepsButton.setEffect(dropShadow);
        OneStepButton.setEffect(dropShadow);


        soundButton.setOnAction(e -> {
            if(soundEnabled) {
                soundEnabled = !soundEnabled;
                soundButton.setStyle("-fx-background-color: #404f6c;-fx-font-size:15");
                soundButton.setText("音效关闭");
                cardMatrixPane.requestFocus();
            }
            else{
                soundEnabled = !soundEnabled;
                soundButton.setStyle("-fx-background-color: #c7ebc3;-fx-font-size:15");
                soundButton.setText("音效开启");
                cardMatrixPane.requestFocus();
            }


        });


        //调整尺寸,相对尺寸


        OneStepButton.setMinSize(80, 80);

        AllStepsButton.setMinSize(80, 80);

        //尺寸改为相对窗口的尺寸
        OneStepButton.prefWidthProperty().bind(cardMatrixPane.widthProperty().divide(6));
        OneStepButton.prefHeightProperty().bind(cardMatrixPane.heightProperty().divide(10));
        AllStepsButton.prefWidthProperty().bind(cardMatrixPane.widthProperty().divide(6));
        AllStepsButton.prefHeightProperty().bind(cardMatrixPane.heightProperty().divide(10));

        //设置内边距
        OneStepButton.setPadding(new Insets(10, 10, 10, 10));
        AllStepsButton.setPadding(new Insets(10, 10, 10, 10));





        //初始化VBox布局

        VBox verticalButtons = new VBox(10, OneStepButton, AllStepsButton,toolMenu.getLayout(),soundButton); // 上、下排列
        menu = verticalButtons;


        OneStepButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            if(cardMatrixPane.getGame().getCardMatrixPane().getCols() != 4 ) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setHeaderText(null);
                alert.setContentText("AI推荐仅支持4*4模式");
                alert.showAndWait();
                return;
            }
            cardMatrixPane.beforeAction(); // Before action logic
            gameAI.move(cardMatrixPane);
            cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
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
            if(cardMatrixPane.getGame().getCardMatrixPane().getCols() != 4 ) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setHeaderText(null);
                alert.setContentText("AI推荐仅支持4*4模式");
                alert.showAndWait();
                return;
            }

            AllStepsButton.setStyle("-fx-background-color: #32c9a1;-fx-font-size:15");
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

                    aiProcessed = true;
                    return;
                }
                aiProcessed = true;

                gameAI.move(cardMatrixPane);
                cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
                if(cardMatrixPane.isGameOver()) {// After action logic

                    AllStepsButton.setStyle("-fx-background-color: #A8C0E4;-fx-font-size:15");
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
            AllStepsButton.setStyle("-fx-background-color: #A8C0E4;-fx-font-size:15");
            AllStepsButton.setText("电脑托管");
            cardMatrixPane.requestFocus();
            AllStepsButton.setOnAction(e -> {
                AllStepsButton.setStyle("-fx-background-color: #32c9a1;-fx-font-size:15");
                AllStepsButton.setText("STOP");
                AllStepsButton();
            });
        });
    }


    public VBox getLayout() {
        return menu;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }


}
