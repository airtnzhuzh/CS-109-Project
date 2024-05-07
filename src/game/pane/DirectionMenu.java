package edu.sustech.game.pane;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// 这是一个用于处理按钮点击事件的外部类




// JavaFX 应用程序
public class DirectionMenu  {
    private CardMatrixPane cardMatrixPane;
//    private HBox menu;
private VBox menu;

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

        // 设置事件处理程序
        upButton.setOnAction(e -> cardMatrixPane.goUp());
        downButton.setOnAction(e -> cardMatrixPane.goDown());
        leftButton.setOnAction(e -> cardMatrixPane.goLeft());
        rightButton.setOnAction(e -> cardMatrixPane.goRight());

// 初始化 HBox 布局
//        menu = new HBox(10); // 间距 10
//        menu.getChildren().addAll(leftButton, upButton, downButton, rightButton);

        HBox horizontalButtons = new HBox(100, leftButton, rightButton); // 左右排列
        VBox verticalButtons = new VBox(10, upButton, horizontalButtons, downButton); // 上、中、下排列

        // 居中对齐
        verticalButtons.setAlignment(Pos.CENTER);

        menu = verticalButtons;


    }

    public VBox getLayout() {
        return menu;
    }









}
