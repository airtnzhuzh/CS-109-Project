package edu.sustech.game.pane;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        // 调整尺寸
        upButton.setMinSize(80, 80);
        downButton.setMinSize(80, 80);
        leftButton.setMinSize(80, 80);
        rightButton.setMinSize(80, 80);


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
                cardMatrixPane.goUp();        // Main action logic
            cardMatrixPane.afterAction();
            } // After action logic
            if(cardMatrixPane.isGameOver()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setContentText("游戏结束,本次最大数字为" + cardMatrixPane.getMaxCard().getNumber() + ",可在菜单栏选择重新开始\n");
                alert.show();
            }
        });

        downButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testDown()) {
                cardMatrixPane.goDown();       // Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
            if(cardMatrixPane.isGameOver()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setContentText("游戏结束,本次最大数字为" + cardMatrixPane.getMaxCard().getNumber() + ",可在菜单栏选择重新开始\n");
                alert.show();
            }
        });

        leftButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testLeft()) {
                cardMatrixPane.goLeft();       // Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
            if(cardMatrixPane.isGameOver()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setContentText("游戏结束,本次最大数字为" + cardMatrixPane.getMaxCard().getNumber() + ",可在菜单栏选择重新开始\n");
                alert.show();
            }
        });

        rightButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            if (cardMatrixPane.testRight()) {
                cardMatrixPane.goRight();      // Main action logic
                cardMatrixPane.afterAction();  // After action logic
            }
            if(cardMatrixPane.isGameOver()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(alert.getAlertType().toString());
                alert.setContentText("游戏结束,本次最大数字为" + cardMatrixPane.getMaxCard().getNumber() + ",可在菜单栏选择重新开始\n");
                alert.show();
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
