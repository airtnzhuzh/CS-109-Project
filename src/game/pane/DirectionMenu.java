package edu.sustech.game.pane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// 这是一个用于处理按钮点击事件的外部类
class DirectionHandler {
    void moveUp() {
        System.out.println("Moving up!");
    }

    void moveDown() {
        System.out.println("Moving down!");
    }

    void moveLeft() {
        System.out.println("Moving left!");
    }

    void moveRight() {
        System.out.println("Moving right!");
    }
}

// JavaFX 应用程序
public class DirectionMenu extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 创建外部处理程序对象
        DirectionHandler handler = new DirectionHandler();

        // 创建按钮
        Button upButton = new Button("Up");
        Button downButton = new Button("Down");
        Button leftButton = new Button("Left");
        Button rightButton = new Button("Right");

        // 设置事件处理程序
        upButton.setOnAction(e -> handler.moveUp());
        downButton.setOnAction(e -> handler.moveDown());
        leftButton.setOnAction(e -> handler.moveLeft());
        rightButton.setOnAction(e -> handler.moveRight());

        // 使用 HBox 布局来排列按钮
        HBox menu = new HBox(10); // 布局中10个单位的间距
        menu.getChildren().addAll(leftButton, upButton, downButton, rightButton);

        // 创建 BorderPane 布局
        BorderPane root = new BorderPane();
        root.setBottom(menu); // 将 HBox 添加到 BorderPane 的底部

        // 创建场景并设置在舞台上
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Direction Menu Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
