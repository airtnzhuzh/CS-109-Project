package edu.sustech.game.app;

import edu.sustech.game.config.GameSaver;
import edu.sustech.game.pane.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

/**
 * 2048运行类
 *
 */
public class Game implements GameCallbacks {
	private BorderPane borderPane;
	private GameMenuBar menuBar;
	private CardMatrixPane cardMatrixPane;
	private String username;

	private GameAI gameAI;

	private DirectionMenu directionMenu;
	
	public Game(Application app, String username) {
		Stage stage = new Stage();
		this.username = username;

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

		//右上角增加GameAI
		gameAI =new GameAI(cardMatrixPane);
		borderPane.setLeft(gameAI.getLayout());
		borderPane.setPadding(new Insets(5,5,5,5));

		//Right方向按钮
		directionMenu = new DirectionMenu(cardMatrixPane);
		borderPane.setRight(directionMenu.getLayout());
		borderPane.setPadding(new Insets(5,5,5,5));

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
		
//		startGame();
//		cardMatrixPane.testColors();//颜色测试
	}
	/*
	public static void main(String[] args) {
		Application.launch(args);
	}*/
	
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
	public void afterResetGridSize(int cols,int rows) {
		cardMatrixPane=new CardMatrixPane(cols,rows,this);
		cardMatrixPane.setPadding(new Insets(5,5,5,5));//外边距
		borderPane.setCenter(cardMatrixPane);
		startGame();
//		cardMatrixPane.testColors();//颜色测试
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
	
	@Override
	public void getPastRecords() {
		menuBar.getScoreMenu().setText("分数: "+cardMatrixPane.getScore());
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.setTitle(alert.getAlertType().toString());
		alert.setContentText("还没有制作喵");
		alert.show();
	}
}

