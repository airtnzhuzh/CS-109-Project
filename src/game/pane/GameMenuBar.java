package edu.sustech.game.pane;

import edu.sustech.game.app.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
 
/**
 * 2048游戏菜单栏
 *
 */
public class GameMenuBar extends MenuBar {
	private GameCallbacks mCallbacks;
	private Menu scoreMenu;
	
	public GameMenuBar(Game game) {//application供回调方法使用
		mCallbacks=(GameCallbacks)game;

		//Game菜单
		Menu gameMenu=new Menu("游戏");//游戏
		MenuItem restartMenuItem=new MenuItem("重新开始");//重新开始
		restartMenuItem.setOnAction(e->mCallbacks.afterRestart());
		MenuItem saveMenuItem=new MenuItem("保存");//保存
		saveMenuItem.setOnAction(e->mCallbacks.save());
		MenuItem logoutMenuItem=new MenuItem("切换账号");//切换账号
		logoutMenuItem.setOnAction(e->mCallbacks.logout() );
		MenuItem exitMenuItem=new MenuItem("关闭游戏");//关闭游戏
		exitMenuItem.setOnAction(e->mCallbacks.save());
		exitMenuItem.setOnAction(e->Platform.exit());
		gameMenu.getItems().addAll(restartMenuItem,saveMenuItem,logoutMenuItem,exitMenuItem);
		
		//Setting菜单
		Menu settingMenu=new Menu("设置");//设置
		ToggleGroup tg=new ToggleGroup();//组
		
		RadioMenuItem r44MenuItem=new RadioMenuItem("尺寸:4x4");
		r44MenuItem.setOnAction(e->mCallbacks.afterResetGridSize(4,4));
		RadioMenuItem r55MenuItem=new RadioMenuItem("尺寸:5x5");
		r55MenuItem.setOnAction(e->mCallbacks.afterResetGridSize(5,5));
		RadioMenuItem r66MenuItem=new RadioMenuItem("尺寸:6x6");
		r66MenuItem.setOnAction(e->mCallbacks.afterResetGridSize(6 ,6));
		
		r44MenuItem.setToggleGroup(tg);
		r55MenuItem.setToggleGroup(tg);
		r66MenuItem.setToggleGroup(tg);
		settingMenu.getItems().addAll(r44MenuItem,r55MenuItem,r66MenuItem);
		r44MenuItem.setSelected(true);//默认选中4x4
		
		//Info菜单
		Menu infoMenu=new Menu("信息");//信息
		MenuItem helpMenuItem=new MenuItem("帮助");//帮助
		helpMenuItem.setOnAction(e->{
			Alert alert=new Alert(AlertType.INFORMATION);
			alert.setTitle(alert.getAlertType().toString());
			alert.setContentText("操作方式:\n"+
					"向上滑动:方向键↑或键W\n"+
					"向下滑动:方向键↓或键S\n"+
					"向左滑动:方向键←或键A\n"+
					"向右滑动:方向键→或键D\n"+
					"\n游戏规则:\n"+
					"相同数字的卡片在靠拢、相撞时会合并\n"+
					"在操作中合并的卡片会以红色边框凸显\n尽可能获得更大的数字!");
			alert.show();
		});
		infoMenu.getItems().addAll(helpMenuItem);
		
		//Record菜单
		Menu recordMenu=new Menu("记录");//记录
		MenuItem historyScoreMenuItem=new MenuItem("历史分数");//历史分数
		historyScoreMenuItem.setOnAction(e->mCallbacks.getPastRecords());
		recordMenu.getItems().addAll(historyScoreMenuItem);

		//Backgroud菜单
		Menu backgroundMenu=new Menu("背景");//背景
		// Create menu items with different backgrounds
		MenuItem backgroundItem1 = new MenuItem("白色简约");
		MenuItem backgroundItem2 = new MenuItem("木质经典");
		MenuItem backgroundItem3 = new MenuItem("条纹色彩");
		MenuItem backgroundItem4 = new MenuItem("流星动感");


		// Add event handlers to menu items
		backgroundItem1.setOnAction(e -> game.setBackground(0));
		backgroundItem2.setOnAction(e -> game.setBackground(1));
		backgroundItem3.setOnAction(e -> game.setBackground(2));
		backgroundItem4.setOnAction(e -> game.setBackground(3));
		backgroundMenu.getItems().addAll(backgroundItem1, backgroundItem2, backgroundItem3, backgroundItem4);
		//棋子风格菜单
		Menu girdStyleMenu=new Menu("棋子风格");//背景
// Create menu items with different backgrounds
		MenuItem girdStyleItem1 = new MenuItem("经典");
		MenuItem girdStyleItem2 = new MenuItem("森林");
		MenuItem girdStyleItem3 = new MenuItem("海洋");
		MenuItem girdStyleItem4 = new MenuItem("暗淡");

// Add event handlers to menu items
		girdStyleItem1.setOnAction(e -> {CardPane.setGirdStyle(0);
			game.getCardMatrixPane().reDraw();
		});
		girdStyleItem2.setOnAction(e -> {CardPane.setGirdStyle(1);
			game.getCardMatrixPane().reDraw();
		});
		girdStyleItem3.setOnAction(e -> {CardPane.setGirdStyle(2);
			game.getCardMatrixPane().reDraw();
		});
		girdStyleItem4.setOnAction(e -> {CardPane.setGirdStyle(3);
			game.getCardMatrixPane().reDraw();
		});
		girdStyleMenu.getItems().addAll(girdStyleItem1, girdStyleItem2, girdStyleItem3, girdStyleItem4);




		//Score菜单
		scoreMenu=new Menu("分数");//分数
		MenuItem moreScoreInfo=new MenuItem("更多分数信息");//更多分数信息
		moreScoreInfo.setOnAction(e->mCallbacks.afterGetMoreScoreInfo());
		scoreMenu.getItems().addAll(moreScoreInfo);
		
		getMenus().addAll(gameMenu,settingMenu,infoMenu,recordMenu,scoreMenu,backgroundMenu,girdStyleMenu);
	}
	
	/**获取分数菜单*/
	public Menu getScoreMenu() {
		return scoreMenu;
	}
}

