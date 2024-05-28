package edu.sustech.game.app;

import edu.sustech.game.config.GameSaver;
import edu.sustech.game.pane.GameAIMenu;
import edu.sustech.game.pane.*;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 2048运行类
 *
 */
public class Game implements GameCallbacks {
	private BorderPane borderPane;
	private GameMenuBar menuBar;
	private CardMatrixPane cardMatrixPane;
	private String username;

	private GameAIMenu gameAIMenu;
	private Stage loginStage;

	private DirectionMenu directionMenu;
	private Stage stage;
	private Background background1;
	private Background background2;
	private Background background3;
	private Background background4;


	
	public Game(Application app, String username,Stage loginStage) {
		stage = new Stage();
		this.username = username;
		this.loginStage = loginStage;

		borderPane=new BorderPane();
		Scene scene=new Scene(borderPane,1000,600);
		
		//Top菜单栏
		menuBar=new GameMenuBar(this);//创建菜单栏,并传入Application供调用
		borderPane.setTop(menuBar);//顶部




		//Center2048卡片矩阵
		if (username == null || !new GameSaver(username).exist()) {
			cardMatrixPane=new CardMatrixPane(this);
		}else {
			cardMatrixPane=new CardMatrixPane(this, false);
		}
		cardMatrixPane.setPadding(new Insets(5,5,5,5));//外边距
		borderPane.setCenter(cardMatrixPane);//中心

		//左上角增加GameAIMenu
		gameAIMenu =new GameAIMenu(cardMatrixPane);
		borderPane.setLeft(gameAIMenu.getLayout());
		borderPane.setPadding(new Insets(5,5,5,5));

		// 加载背景图片
		Image backgroundImage1 = new Image("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\background01.png"); // 替换为您图片的路径
		Image backgroundImage2 = new Image("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\background02.png");
		Image backgroundImage3 = new Image("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\background03.png");
		Image backgroundImage4 = new Image("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\background04.gif");
		// 创建背景图像对象
		BackgroundImage backgroundImg1 = new BackgroundImage(
				backgroundImage1,
				BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		BackgroundImage backgroundImg2 = new BackgroundImage(
				backgroundImage2,
				BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		BackgroundImage backgroundImg3 = new BackgroundImage(
				backgroundImage3,
				BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		BackgroundImage backgroundImg4 = new BackgroundImage(
				backgroundImage4,
				BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT,
				new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));

		// 创建背景对象
		background1 = new Background(backgroundImg1);
		background2 = new Background(backgroundImg2);
		background3 = new Background(backgroundImg3);
		background4 = new Background(backgroundImg4);


		borderPane.setBackground(background1);




		//Right方向按钮
		directionMenu = new DirectionMenu(cardMatrixPane);
		directionMenu.setPadding(new Insets(5,5,5,5));
		borderPane.setRight(directionMenu.getLayout());

		borderPane.setPadding(new Insets(0,0,10,10));

		stage.setTitle("2048");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if(username != null){
					save();
				}
			}
		});




		// 显示步数step，时刻刷新
		Label step = new Label("步数: " + cardMatrixPane.getStep());
	    step.setFont(Font.font(20));
		step.setStyle("-fx-background-color: #7bcc4c; -fx-padding: 10px;-fx-background-radius: 20px");
		// 添加阴影效果
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5);
		dropShadow.setOffsetX(3);
		dropShadow.setOffsetY(3);
		dropShadow.setColor(Color.GRAY);
		step.setEffect(dropShadow);

		//定时保存
		Label save = new Label();
		save.setVisible(false);
		save.setFont(Font.font(20));
		save.setStyle("-fx-background-color: #7bcc4c; -fx-padding: 10px;-fx-background-radius: 20px");
		// 添加阴影效果
		save.setEffect(dropShadow);



		HBox bottomBox = new HBox(10,step,save);


		borderPane.setBottom(bottomBox);
		Timer timer2 = new Timer();
		timer2.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					step.setText("步数: " + cardMatrixPane.getStep());
				});
			}
		}, 0, 100);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					if(username!= null) {
						autosave();
						save.setText("已自动保存");
						save.setVisible(true);
						FadeTransition ft = new FadeTransition(Duration.millis(1000), save);
						ft.setFromValue(0);
						ft.setToValue(1);
						ft.setCycleCount(1);
						ft.setAutoReverse(true);
						ft.play();
						//4秒后隐藏
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								Platform.runLater(() -> {
									save.setVisible(false);
								});
							}
						}, 4000);
					}
					});
			}
		}, 0, 20000); // 每20秒刷新一次





	}




	public void setBackground(int i){
		if(i == 0) {
			borderPane.setBackground(background1);
		}
		if(i == 1) {
			borderPane.setBackground(background2);
		}
		if(i == 2) {
			borderPane.setBackground(background3);
		}
		if(i == 3) {
			borderPane.setBackground(background4);
		}

	}
	/**开始游戏*/
	public void startGame() {
		cardMatrixPane.requestFocus();//添加焦点
		cardMatrixPane.createKeyListener();//添加键盘监听
		afterScoreChange();
	}

	public String getUsername() {
		return username;
	}
 
	@Override
	public void afterRestart() {
		cardMatrixPane.restartMatrix();
	}

	@Override
	public void logout() {
		if(username!= null){
			try {
				new GameSaver(username).save(cardMatrixPane);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		stage.close();
		loginStage.show();
	}
 
	@Override
	public void afterResetGridSize(int cols,int rows) {
		cardMatrixPane=new CardMatrixPane(cols,rows,this);
		cardMatrixPane.setPadding(new Insets(5,5,5,5));//外边距
		borderPane.setCenter(cardMatrixPane);
		startGame();
//		cardMatrixPane.testColors();//颜色测试
	}
	public CardMatrixPane getCardMatrixPane() {
		return cardMatrixPane;
	}

	public GameAIMenu getToolMenu() {
		return gameAIMenu;
	}
 
	@Override
	public void afterScoreChange() {
		menuBar.getScoreMenu().setText("分数: "+cardMatrixPane.getScore());
	}
 
	@Override
	public void afterGetMoreScoreInfo() {
		int[] temp=cardMatrixPane.getMcQuantities();
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle(alert.getAlertType().toString());
		alert.setContentText(
				"4的合并次数: 		"+temp[0]+"\n"+
				"8的合并次数: 		"+temp[1]+"\n"+
				"16的合并次数: 		"+temp[2]+"\n"+
				"32的合并次数: 		"+temp[3]+"\n"+
				"64的合并次数: 		"+temp[4]+"\n"+
				"128的合并次数:		"+temp[5]+"\n"+
				"256的合并次数:		"+temp[6]+"\n"+
				"512的合并次数:		"+temp[7]+"\n"+
				"1024的合并次数: 	"+temp[8]+"\n"+
				"2048的合并次数: 	"+temp[9]+"\n"+
				"4096的合并次数: 	"+temp[10]+"\n"+
				"8192的合并次数: 	"+temp[11]+"\n"+
				"16384的合并次数: 	"+temp[12]+"\n"+
				"32768的合并次数: 	"+temp[13]+"\n"+
				"65536的合并次数: 	"+temp[14]+"\n");
		alert.show();
	}
	
	@Override
	public void save()  {
		//实现保存代码
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle(alert.getAlertType().toString());
		if(username== null){
			alert.setContentText("对不起，游客不可以保存进度！");
			alert.show();
		}else {
			try {
				new GameSaver(username).save(cardMatrixPane);
				alert.setContentText("保存成功");
			} catch (IOException ex) {
				alert.setContentText("保存失败");
			}
			alert.show();
		}
	}

	public void autosave()  {
		//实现保存代码
		if(username!= null){
			try {
				new GameSaver(username).save(cardMatrixPane);
			} catch (IOException ex) {
			}
		}
	}

	@Override
	public void getPastRecords() {
		List<String> record=null;
		try {
			record = cardMatrixPane.getRecord();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle(alert.getAlertType().toString());
		if (record != null) {
			int i=1;
			StringBuffer sb = new StringBuffer();
			for (String str : record) {
				sb.append(i +"、"+ str + "\n");
				i++;
				if (i==21){
					break;
				}
			}
			alert.setContentText(sb.toString());
		}else {
			alert.setContentText("还没有历史得分哦");
		}
		alert.show();
	}


}

