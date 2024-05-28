package edu.sustech.game.pane;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

// 这是一个用于处理按钮点击事件的外部类




// JavaFX 应用程序
public class DirectionMenu extends Pane {
    private CardMatrixPane cardMatrixPane;
//    private HBox menu;
private  VBox menu;


    public DirectionMenu(CardMatrixPane cardMatrixPane) {

        // 创建按钮
        Button upButton = new Button("Up");
        Button downButton = new Button("Down");
        Button leftButton = new Button("Left");
        Button rightButton = new Button("Right");

        // 添加阴影效果
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.GRAY);
        upButton.setEffect(dropShadow);
        downButton.setEffect(dropShadow);
        leftButton.setEffect(dropShadow);
        rightButton.setEffect(dropShadow);






        // 调整尺寸
        upButton.setMinSize(80, 80);
        downButton.setMinSize(80, 80);
        leftButton.setMinSize(80, 80);
        rightButton.setMinSize(80, 80);

        //设置字体大小
        upButton.setStyle("-fx-font-size:20");
        downButton.setStyle("-fx-font-size: 20");
        leftButton.setStyle("-fx-font-size: 20");
        rightButton.setStyle("-fx-font-size: 20");










// 初始化 HBox 布局
//        menu = new HBox(10); // 间距 10
//        menu.getChildren().addAll(leftButton, upButton, downButton, rightButton);

        HBox horizontalButtons = new HBox(100, leftButton, rightButton); // 左右排列
        VBox verticalButtons = new VBox(10, upButton, horizontalButtons, downButton); // 上、中、下排列
        // 居中对齐
        verticalButtons.setAlignment(Pos.CENTER);
        menu = verticalButtons;
        upButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testUp())  {
                cardMatrixPane.goUp();
                cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
            cardMatrixPane.afterAction();
            } // After action logic
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
            }
        });

        downButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testDown()) {
                cardMatrixPane.goDown();
                cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
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
            }
        });

        leftButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testLeft()) {
                cardMatrixPane.goLeft();
                cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
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
            }
        });

        rightButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testRight()) {
                cardMatrixPane.goRight();
                cardMatrixPane.setStep(cardMatrixPane.getStep()+1);// Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
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
            }
        });
        // 设置事件处理程序
//        setOnAction(e -> {//鼠标按下事件
//            System.out.println("test05");
//            if (!cardMatrixPane.beforeAction()) return;//动作前
//
//            Button mb = (Button) e.getTarget();
//            if (mb.equals(upButton)) {
//                System.out.println("goUp02");
//                cardMatrixPane.goUp();//↑
//            } else if (mb.equals(downButton)) {
//                cardMatrixPane.goDown();//↓
//            } else if (mb.equals(leftButton)) {
//                cardMatrixPane.goLeft();//←
//            } else if (mb.equals(rightButton)) {
//                cardMatrixPane.goRight();//→
//            } else {
//                return;//未定义的操作
//            }
// });



    }
    public VBox getLayout() {
        return menu;
    }
}
