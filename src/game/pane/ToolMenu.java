package edu.sustech.game.pane;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.Timer;
import java.util.TimerTask;

public class ToolMenu extends Pane {
    private CardMatrixPane cardMatrixPane;
    //    private HBox menu;
    private VBox menu;
    private Button removeButton;


    public ToolMenu(CardMatrixPane cardMatrixPane) {
        this.cardMatrixPane = cardMatrixPane;




        // 创建按钮
        removeButton = new Button("清除方块"+"\n"+"次数：" + cardMatrixPane.getUsecount());
        Button guangGao = new Button("观看广告\n获得道具次数");
        removeButton.setMinSize(80, 80);
        guangGao.setMinSize(80, 80);

        removeButton.prefWidthProperty().bind(cardMatrixPane.widthProperty().divide(6));
        removeButton.prefHeightProperty().bind(cardMatrixPane.heightProperty().divide(10));
        guangGao.prefWidthProperty().bind(cardMatrixPane.widthProperty().divide(6));
        guangGao.prefHeightProperty().bind(cardMatrixPane.heightProperty().divide(10));
        removeButton.setStyle("-fx-background-color: #859FC8;-fx-font-size:15");
        guangGao.setStyle("-fx-background-color: #637DAD;-fx-font-size:13");

        // 添加阴影效果
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.GRAY);
        removeButton.setEffect(dropShadow);
        guangGao.setEffect(dropShadow);



        removeButton.setMinSize(80, 80);

        VBox Buttons = new VBox(10, removeButton,guangGao);
        menu = Buttons;

        guangGao.setOnAction(e -> {
            displayAd(removeButton,cardMatrixPane);
        });


        removeButton.setOnAction(e -> {
            cardMatrixPane.requestFocus();
            if(cardMatrixPane.getUsecount()>=1) {
                cardMatrixPane.getGridPane().setMouseTransparent(false);
                cardMatrixPane.setUsecount(cardMatrixPane.getUsecount() - 1);
            }
            //重画
            removeButton.setText("清除方块"+"\n"+"次数：" + cardMatrixPane.getUsecount());
        });

        // 启动定时器定时刷新按钮文本
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    removeButton.setText("清除方块" + "\n" + "次数：" +cardMatrixPane.getUsecount());
                });
            }
        }, 0, 1000); // 每秒钟刷新一次







    }
    public void updateRemoveButtonText() {
        removeButton.setText("清除方块"+"\n"+"次数：" + cardMatrixPane.getUsecount());
    }





    public VBox getLayout() {
        return menu;
    }
    private void displayAd(Button button,CardMatrixPane cardMatrixPane) {
        Stage adStage = new Stage();
        BorderPane adRoot = new BorderPane();

        // 加载图片
        Image image = new Image("file:C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\GuangGao.png"); // 替换为您图片的路径
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);

        // 显示倒计时
        Text countdownText = new Text("");
        countdownText.setFont(Font.font(20));
        BorderPane.setAlignment(countdownText, Pos.TOP_RIGHT);

        adRoot.setCenter(imageView);
        adRoot.setTop(countdownText);

        // 创建并启动倒计时
        Countdown countdown = new Countdown(adStage,cardMatrixPane,button);
        countdown.start();

        Scene adScene = new Scene(adRoot, 600, 600);
        adStage.setScene(adScene);
        adStage.setTitle("广告");
        adStage.show();




    }





}
 class Countdown extends Thread {
    private Stage stage;
    private CardMatrixPane cardMatrixPane;
    private Button button;
    private ToolMenu toolMenu;


    public Countdown(Stage stage, CardMatrixPane cardMatrixPane,Button button) {
        this.stage = stage;
        this.cardMatrixPane = cardMatrixPane;
        this.button = button;
        this.toolMenu = new ToolMenu(cardMatrixPane);


    }

    @Override
    public void run() {
        try {
            for (int i = 12; i > 0; i--) {
                Thread.sleep(1000); // 每秒钟减一
                final int count = i;
                // 更新倒计时文本
                javafx.application.Platform.runLater(() -> {
                    ((BorderPane) stage.getScene().getRoot()).getTop().setVisible(true);
                    ((Text) ((BorderPane) stage.getScene().getRoot()).getTop()).setText("倒计时 " + (count-2) + " 秒钟");
                });
            }
            cardMatrixPane.setUsecount(cardMatrixPane.getUsecount() + 2);



            // 关闭窗口
            javafx.application.Platform.runLater(() -> stage.close());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




}

