package edu.sustech.game.pane;

import java.util.ArrayList;
import java.util.List;
 
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
 
/**
 * 卡片矩阵
 * 
 */
//若继承自Pane类,缺少需要的setAlignment()方法
public class CardMatrixPane extends StackPane {
	private GameCallbacks mCallbacks;
	private int cols;//卡片矩阵列数
	private int rows;//卡片矩阵行数
	private GridPane gridPane;//卡片矩阵容器
	private CardPane[][] cps;//卡片矩阵
	private long score=0;//分数,初始为0
	private int[] mcQuantities=new int[15];//合并过的卡片数字数量,包括4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536
	
	
	public CardMatrixPane(Application application) {
		this(4,4,application);//默认4*4
	}
	
	public CardMatrixPane(int cols,int rows,Application application) {//application供回调方法使用
		mCallbacks=(GameCallbacks)application;
		this.cols=cols;
		this.rows=rows;
//		this.setBackground(new Background(new BackgroundFill(Color.BLUE,CornerRadii.EMPTY,Insets.EMPTY)));//测试用
		init();
		getChildren().add(gridPane);
	}
	
	/**获取分数*/
	public long getScore() {
		return score;
	}
	
	/**获取合并过的卡片数字数量*/
	public int[] getMcQuantities() {
		return mcQuantities;
	}
	
	private void init() {
		initGridPane();//初始化GridPane
		createRandomNumber();//在随机的空卡片上生成数字,这个方法会返回布尔值,但这里不需要
	}
	
	/**初始化GridPane*/
	private void initGridPane() {
		gridPane=new GridPane();
//		gridPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,CornerRadii.EMPTY,Insets.EMPTY)));//测试用
//		gridPane.setGridLinesVisible(true);//单元格边框可见,测试用
		
		//对this尺寸监听
		widthProperty().addListener(ov->setGridSizeAndCardFont());//宽度变化,更新边长和字号
		heightProperty().addListener(ov->setGridSizeAndCardFont());//高度变化,更新边长和字号
		//单元格间隙
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		//绘制每个单元格
		cps=new CardPane[cols][rows];
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=new CardPane(0);
				gridPane.add(card, i, j);
				cps[i][j]=card;
			}
		}
	}
	
	/**设置GridPane的边长,其内部单元格的尺寸和CardPane的字号*/
	private void setGridSizeAndCardFont(){
		double w=widthProperty().get();
		double h=heightProperty().get();
		double min= Math.min(w, h);
		gridPane.setMaxWidth(min);
		gridPane.setMaxHeight(min);
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				card.getLabel().setFont(new Font((min/14)/cols*4));//设置显示数字的尺寸
				//由于下面两行代码主动设置了每个单元格内cardPane的尺寸,gridPane不需要自动扩张
				card.setPrefWidth(min-5*(cols-1));//设置单元格内cardPane的宽度,否则它会随其内容变化,进而影响单元格宽度
				card.setPrefHeight(min-5*(rows-1));//设置单元格内cardPane的高度,否则它会随其内容变化,进而影响单元格高度
			}
		}
	}
	
	/**添加键盘监听*/
	public void createKeyListener() {
		setOnKeyPressed(e->{//键盘按下事件
			CardPane maxCard=getMaxCard();//最大卡片
			if(maxCard.getType()==16) {//出现最大数字
				Alert alert=new Alert(AlertType.INFORMATION);
				alert.setTitle(alert.getAlertType().toString());
				alert.setContentText("恭喜你,游戏的最大数字为"+maxCard.getNumber()+",可在菜单栏选择重新开始\n"+
						"事实上,我们还尚未准备比"+maxCard.getNumber()+"更大的数字卡片,终点已至");
				alert.show();
				return;
			}
			KeyCode kc=e.getCode();
			switch(kc) {
			case UP:
			case W:
				goUp();//↑
				break;
			case DOWN:
			case S:
				goDown();//↓
				break;
			case LEFT:
			case A:
				goLeft();//←
				break;
			case RIGHT:
			case D:
				goRight();//→
				break;
			default:
				return;//未定义的操作
			}
			redrawAllCardsAndResetIsMergeAndSetScore();//重绘所有的卡片,并重设合并记录,更新分数
			boolean isFull=!createRandomNumber();//生成新的随机数字卡片,并判满,这包含了生成数字后满的情况
			if(isFull) {//矩阵已满,可能已经游戏结束
				boolean testOpe=false;//是否还能进行横向或竖向操作
				testOpe|=testUp();//还能进行竖向操作
				testOpe|=testLeft();//还能进行横向操作
				if(!testOpe) {//游戏结束
					Alert alert=new Alert(AlertType.INFORMATION);
					alert.setTitle(alert.getAlertType().toString());
					alert.setContentText("游戏结束,本次最大数字为"+maxCard.getNumber()+",可在菜单栏选择重新开始\n");
					alert.show();
				}
			}
		});
	}
	
	/**向上操作*/
	 void goUp() {
		boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
		do {
			mergeOrMoveExist=false;//初始为false
			for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
				for(int j=1;j<rows;j++) {//从第二行起向下,遍历卡片矩阵的行
					CardPane card=cps[i][j];
					CardPane preCard=cps[i][j-1];//前一个卡片
					boolean isChanged=card.tryMergeOrMoveInto(preCard);//记录两张卡片间是否进行了移动或合并
					mergeOrMoveExist|=isChanged;//只要有一次移动或合并记录,就记存在为true
				}
			}
		}while(mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
	}
	
	/**测试是否能向上操作*/
	private boolean testUp() {
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=1;j<rows;j++) {//从第二行起向下,遍历卡片矩阵的行
				CardPane card=cps[i][j];
				CardPane preCard=cps[i][j-1];//前一个卡片
				if(card.canMergeOrMove(preCard)) {
					return true;//能
				}
			}
		}
		return false;//不能
	}
	
	/**向下操作*/
	 void goDown() {
		boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
		do {
			mergeOrMoveExist=false;//初始为false
			for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
				for(int j=rows-2;j>=0;j--) {//从倒数第二行起向上,遍历卡片矩阵的行
					CardPane card=cps[i][j];
					CardPane preCard=cps[i][j+1];//前一个卡片
					boolean isChanged=card.tryMergeOrMoveInto(preCard);//记录两张卡片间是否进行了移动或合并
					mergeOrMoveExist|=isChanged;//只要有一次移动或合并记录,就记存在为true
				}
			}
		}while(mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
	}
	
	/**向左操作*/
	void goLeft() {
		boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
		do {
			mergeOrMoveExist=false;//初始为false
			for(int i=1;i<cols;i++) {//从第二列起向右,遍历卡片矩阵的列
				for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
					CardPane card=cps[i][j];
					CardPane preCard=cps[i-1][j];//前一个卡片
					boolean isChanged=card.tryMergeOrMoveInto(preCard);//记录两张卡片间是否进行了移动或合并
					mergeOrMoveExist|=isChanged;//只要有一次移动或合并记录,就记存在为true
				}
			}
		}while(mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
	}
	
	/**测试是否能向左操作*/
	private boolean testLeft() {
		for(int i=1;i<cols;i++) {//从第二列起向右,遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				CardPane preCard=cps[i-1][j];//前一个卡片
				if(card.canMergeOrMove(preCard)) {
					return true;//能
				}
			}
		}
		return false;//不能
	}
	
	/**向右操作*/
	void goRight() {
		boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
		do {
			mergeOrMoveExist=false;//初始为false
			for(int i=cols-2;i>=0;i--) {//从倒数第二列起向左,遍历卡片矩阵的列
				for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
					CardPane card=cps[i][j];
					CardPane preCard=cps[i+1][j];//前一个卡片
					boolean isChanged=card.tryMergeOrMoveInto(preCard);//记录两张卡片间是否进行了移动或合并
					mergeOrMoveExist|=isChanged;//只要有一次移动或合并记录,就记存在为true
				}
			}
		}while(mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
	}
	
	/**重绘所有的卡片,并重设合并记录,并设置分数*/
	private void redrawAllCardsAndResetIsMergeAndSetScore() {
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				card.draw();
				if(card.isMerge()) {//这张卡片合并过
					score+=card.getNumber();//计入分数
					mcQuantities[card.getType()-2]++;//相应的合并过的卡片数字数量+1
					card.setMerge(false);
				}
			}
		}
		mCallbacks.afterScoreChange();
	}
	
	/**获取卡片矩阵中的最大卡片*/
	private CardPane getMaxCard() {
		CardPane maxCard=new CardPane();//type=0的新卡片
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				if(card.getType()>maxCard.getType()) {
					maxCard=card;
				}
			}
		}
		return maxCard;
	}
	
	/**在随机的空卡片上生成新的数字,若矩阵已满,或生成数字后满,则返回false*/
	public boolean createRandomNumber() {
		List<CardPane> voidCards=new ArrayList<>();//空卡片列表
		
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				if(card.getType()==0) {//是空卡片
					voidCards.add(card);//添加到列表中
				}
			}
		}
		int len=voidCards.size();
		if(len==0) {//没有空卡片了,返回
			return false;//判满
		}
		int type;
		int index=(int)(Math.random()*5);//0,1,2,3,4
		if(index!=0) {//4/5概率
			type=1;//number=2
//			type=7;//number=128
		}else {//1/5概率
			type=2;//number=4
//			type=8;//number=256
		}
		int voidCardIndex=(int)(Math.random()*len);
		CardPane card=voidCards.get(voidCardIndex);
		card.setType(type);//更新type,生成数字
		card.draw();//重绘此卡片
		if(len==1) {//只有一个空卡片,矩阵生成数字后满
			return false;
		}
		return true;
	}
	
	/**重启卡片矩阵,并在随机的空卡片上生成数字*/
	public void restartMatrix() {
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				card.setType(0);
				card.draw();//重绘
			}
		}
		score=0;//重设分数
		mcQuantities=new int[15];//重设合并过的卡片数字数量
		mCallbacks.afterScoreChange();
		createRandomNumber();//在随机的空卡片上生成数字,这个方法会返回布尔值,但这里不需要
	}
 
	/**进行颜色测试,可在4*4矩阵中显示2至65536*/
	public void testColors() {
		for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
			for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
				CardPane card=cps[i][j];
				int type=i*4+j+1;
				if(type>16) {
					return;
				}
				card.setType(i*4+j+1);
				card.draw();//重绘
			}
		}
	}
}

