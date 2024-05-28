package edu.sustech.game.pane;

import edu.sustech.game.config.CardColor;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.w3c.dom.ls.LSOutput;

/**
 * 数字卡片
 * 
 */
//若继承自Pane类,缺少需要的setAlignment()方法
//若继承自StackPane类,会出现一些绘制错误
public class CardPane extends BorderPane {
	private static final int RC=5;//矩形的圆角
	private static int girdStyle = 0;
	private int type;
	/* 类型
	 * type=0	number=0
	 * type=1	number=2
	 * type=2	number=4
	 * type=3	number=8
	 * ...
	 */
	private boolean merge=false;//是否被合并过,如果合并了,则不能继续合并,针对当前轮
	private Rectangle r;//圆角矩形
	private Label l;//数字标签
	public static void setGirdStyle(int i) {
		girdStyle = i;
	}


	/**无参构造方法*/
	public CardPane() {
		this(0);
	}
	
	/**构造方法,通过下标和类型生成数字卡片*/
	public CardPane(int type) {
		this.type=type;
		//圆角矩形
		r=new Rectangle();
		r.widthProperty().bind(this.widthProperty());//矩形的宽度绑定单元格宽度
		r.heightProperty().bind(this.heightProperty());//矩形的高度绑定单元格高度
		r.setArcWidth(RC);//圆角宽度
		r.setArcHeight(RC);//圆角高度
		r.setStroke(Color.BLACK);//边框颜色
		r.setStrokeWidth(2);//边框宽度
		getChildren().add(r);
		//数字标签
		l=new Label("65536");//65536是4*4情况下可能出现的最大数字
		setCenter(l);
		//绘制变化的部分
		this.draw();

		r.getParent().requestFocus();;
		r.setOnMouseClicked(event->{
			setType(0);
			this.draw();
			getParent().setMouseTransparent(true);
		});
	}


	
	/**获取数字标签对象*/
	public Label getLabel() {
		return l;
	}
	
	/**设置卡片类型*/
	public void setType(int type) {
		this.type=type;
	}
	
	/**获取卡片类型*/
	public int getType() {
		return type;
	}
	
	/**设置合并记录*/
	public void setMerge(boolean merge) {
		this.merge=merge;
	}
	
	/**获取合并记录*/
	public boolean isMerge() {
		return merge;
	}
	
	/**绘制单次操作中卡片变化的部分,包括颜色和显示的数字*/
	public void draw() {
		if(merge) {//突出显示已合并的卡片
     ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(80), r);
     scaleTransition.setToX(1.2f);
     scaleTransition.setToY(1.2f);
     scaleTransition.setCycleCount(2);
     scaleTransition.setAutoReverse(true);
     scaleTransition.play();
			r.setStroke(Color.RED);//此次操作中合并,显示红色
		}else {
			r.setStroke(Color.BLACK);//此次操作中没有合并,显示黑色
		}

		if(girdStyle==0) {
			r.setFill(CardColor.CB[type]);

		}
		else if(girdStyle==1){
			r.setFill(CardColor.FORESTCB[type]);

		}
		else if(girdStyle==2){
			r.setFill(CardColor.OCEANCB[type]);
		}
		else{
			r.setFill(CardColor.DARKCB[type]);
		}
		drawNumber();

	}
	
	/**判断此卡片能否向调用者所给出的卡片移动或合并*/
	public boolean canMergeOrMove(CardPane card) {
		if(type==0) {//空卡片不能移动或合并
			return false;
		}
		if(card.type==0) {//可以向空卡片移动
			return true;
		}
		return type==card.getType()&&!merge&&!card.isMerge();//不能二次合并
	}
	
	/**尝试向调用者所给出的卡片移动或合并,这一函数可能会修改两个卡片的属性*/
	public boolean tryMergeOrMoveInto(CardPane card)  {
		boolean canMergeOrMove=canMergeOrMove(card);
		if(canMergeOrMove) {//可以移动或合并

//			Pane parent = (Pane) this.getParent();
//			CardPane copy = this.createCopy();
//			parent.getChildren().add(copy);
//
//			TranslateTransition slide = new TranslateTransition(Duration.seconds(0.2), copy);
//			slide.setToX(card.getLayoutX() - copy.getLayoutX());
//			slide.setToY(card.getLayoutY() - copy.getLayoutY());



			if (card.getType() == 0) {//移动
				card.setType(type);//移动数字
				card.setMerge(merge);//移动合并记录
				this.toVoid();//this成为空卡片
			} else {//合并
				card.setType(card.getType() + 1);//合并数字
				card.setMerge(true);//设置合并记录
				this.toVoid();//this成为空卡片
			}

//			slide.play();
//			copy.createCopy();
		}

		return canMergeOrMove;
	}


	
	/**刷新为空卡片*/
	private void toVoid() {
		type=0;
		merge=false;
	}
	
	private void drawNumber() {
		if(type==0) {//空卡片
			l.setText("");
		}else {//非空卡片需要显示数字
			l.setText(""+getNumber());
		}
	}
	
	/**计算需显示的数字*/
	public int getNumber() {
		return (int)Math.pow(2,type);
	}
	
	@Override
	public String toString() {
		return "[type="+type+", merge="+merge+"]";
	}

}
