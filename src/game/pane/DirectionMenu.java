package edu.sustech.game.pane;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
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
    Application application;

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
            cardMatrixPane.goUp();        // Main action logic
            cardMatrixPane.afterAction(); // After action logic
        });

        downButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            cardMatrixPane.goDown();       // Main action logic
            cardMatrixPane.afterAction();  // After action logic
        });

        leftButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            cardMatrixPane.goLeft();       // Main action logic
            cardMatrixPane.afterAction();  // After action logic
        });

        rightButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            cardMatrixPane.beforeAction(); // Before action logic
            cardMatrixPane.goRight();      // Main action logic
            cardMatrixPane.afterAction();  // After action logic
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
