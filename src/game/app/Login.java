package edu.sustech.game.app;

import edu.sustech.game.user.UserAuth;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;


public class Login extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Label l_name = new Label("账号:");
        l_name.setFont(Font.font(15));

        Label l_password = new Label("密码:");
        l_password.setFont(Font.font(15));

        TextField t_name = new TextField();
        t_name.setTooltip(new Tooltip("请输入账号"));

        PasswordField p_password = new PasswordField();
        p_password.setTooltip(new Tooltip("请输入密码"));

        /*测试用账号密码t_name.setUserData("aegis");
        p_password.setUserData("2887");*/

        Button login = new Button("登录");
        Button clear = new Button("清除");
        Button register = new Button("注册");
        Button guest = new Button("游客");
        register.setPrefWidth(70);
        login.setPrefWidth(70);
        guest.setPrefWidth(170);

        GridPane gr=new GridPane();

        Image loginBackgroundImage = new Image("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\LoginBackground2.png");
        // 创建背景图像对象
        BackgroundImage loginBackground = new BackgroundImage(
                loginBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        Background background = new Background(loginBackground);
        gr.setBackground(background);

        gr.add(l_name,0,8);
        gr.add(t_name,1,8);
        gr.add(l_password,0,9);
        gr.add(p_password,1,9);
        gr.add(clear,2,8);
        gr.add(register,1,10);
        gr.add(login,1,10);
        gr.add(guest,1,11);

        gr.setHgap(5);
        gr.setVgap(15);

        gr.setMargin(register,new Insets(0,0,0,0));
        gr.setMargin(login,new Insets(0,0,0,100));
        gr.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gr);
        primaryStage.setScene(scene);
        primaryStage.setTitle("登录");
        primaryStage.setWidth(650);
        primaryStage.setHeight(680);



        primaryStage.setResizable(false);
        primaryStage.show();

        clear.setOnAction(event -> {
                t_name.setText("");
                p_password.setText("");
        });


        login.setOnAction(event -> {
            String name = t_name.getText();
            String password = p_password.getText();
            try {
                if (UserAuth.login(name, password)){
                    primaryStage.close();
                    new Game(this,name,primaryStage).startGame();
                    Success success = new Success("登录");
                }else{
                    primaryStage.setTitle("账号或者密码错误");
                    FadeTransition fade = new FadeTransition();
                    fade.setDuration(Duration.seconds(0.1));
                    fade.setNode(gr);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        register.setOnAction(event -> {
                Signup signup = new Signup(primaryStage);
                primaryStage.close();
        });

        guest.setOnAction(event -> {
            primaryStage.close();
            new Game(this,null,primaryStage).startGame();
        });
    }
}

class Signup{
    private final Stage stage = new Stage();
    public Signup(Stage primaryStage){
        Label l_newname = new Label("账号:");
        l_newname.setFont(Font.font(15));

        Label l_newpassword1 = new Label("密码:");
        l_newpassword1.setFont(Font.font(15));

        Label l_newpassword2 = new Label("确认密码:");
        l_newpassword2.setFont(Font.font(15));

        TextField t_newname = new TextField();
        t_newname.setTooltip(new Tooltip("请输入新账号"));

        PasswordField p_newpassword1 = new PasswordField();
        p_newpassword1.setTooltip(new Tooltip("请输入密码"));

        PasswordField p_newpassword2 = new PasswordField();
        p_newpassword2.setTooltip(new Tooltip("请再输入密码"));


        Button clear = new Button("清除");
        Button register = new Button("注册");


        GridPane gr=new GridPane();
        gr.setStyle("-fx-background-color: #FFF0F5");

        gr.add(l_newname,0,0);
        gr.add(t_newname,1,0);
        gr.add(l_newpassword1,0,1);
        gr.add(p_newpassword1,1,1);
        gr.add(l_newpassword2,0,2);
        gr.add(p_newpassword2,1,2);
        gr.add(clear,0,3);
        gr.add(register,1,3);

        gr.setHgap(5);
        gr.setVgap(15);

        gr.setMargin(register,new Insets(0,0,0,60));
        gr.setMargin(register,new Insets(0,0,0,120));
        gr.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gr);
        stage.setScene(scene);
        stage.setTitle("注册新账号");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.setResizable(false);
        stage.show();


        clear.setOnAction(event ->{
                t_newname.setText("");
                p_newpassword1.setText("");
                p_newpassword2.setText("");
        });

        register.setOnAction(event -> {
            String name = t_newname.getText();
            String password1 = p_newpassword1.getText();
            String password2 = p_newpassword2.getText();
            try {
                if (!UserAuth.exist(name)) {
                    if (password1.equals(password2) && UserAuth.signup(name, password1) == true) {
                        primaryStage.show();
                        Success success = new Success("注册");
                        stage.close();
                    } else if (!password1.equals(password2)) {
                        stage.setTitle("两次密码不一致，请重新输入");
                        FadeTransition fade = new FadeTransition();
                        fade.setDuration(Duration.seconds(0.1));
                        fade.setNode(gr);
                        fade.setFromValue(0);
                        fade.setToValue(1);
                        fade.play();
                    }
                }else {
                    stage.setTitle("账号已存在，请重新输入");
                    FadeTransition fade = new FadeTransition();
                    fade.setDuration(Duration.seconds(0.1));
                    fade.setNode(gr);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}


class Success{
    private final Stage stage = new Stage();

    public Success(String act){
        Text text = new Text("已成功" + act + "！");
        BorderPane bor = new BorderPane();
        bor.setStyle("-fx-background-color: #FFF0F5");
        bor.setCenter(text);

        Scene scene = new Scene(bor);
        stage.setScene(scene);
        stage.setTitle(act + "成功");
        stage.setHeight(180);
        stage.setWidth(320);
        stage.show();
    }
}