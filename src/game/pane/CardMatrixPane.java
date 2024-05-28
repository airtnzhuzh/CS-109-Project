package edu.sustech.game.pane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.sustech.game.app.Game;
import edu.sustech.game.config.GameSaver;
import javafx.animation.*;
import javafx.scene.control.Alert;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javax.sound.sampled.*;
import javax.tools.Tool;

/**
 * 卡片矩阵
 */
//若继承自Pane类,缺少需要的setAlignment()方法
public class CardMatrixPane extends StackPane {
    private GameCallbacks mCallbacks;
    private int cols;//卡片矩阵列数
    private int rows;//卡片矩阵行数
    private GridPane gridPane;//卡片矩阵容器
    private CardPane[][] cps;//卡片矩阵
    private long score = 0;//分数,初始为0
    private int step = 0;//步数，初始为0
    private List<String> record = new ArrayList<>();//记录
    private int usecount;
    private int aiuse;




    private Game game;
    private GameSaver gs;



    private boolean keyProcessed = false;//键是否已经处理
    private int[] mcQuantities = new int[15];//合并过的卡片数字数量,包括4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536


    public CardMatrixPane(Game game) {
        this(4, 4, game);//默认4*4
    }

    public CardMatrixPane(Game game, boolean newGame) {


        mCallbacks=(GameCallbacks)game;
        gs = new GameSaver(game.getUsername());
        this.game = game;
        if (newGame) {
            this.cols=4;
            this.rows=4;

            init();
        }else {
            try {
                Map<String, Object> map = gs.getCardPane();
                if (map != null && map.get("cols") != null &&  map.get("rows") != null
                        && map.get("cardPane") != null) {
                    this.cols = (int)map.get("cols");
                    this.rows = (int) map.get("rows");
                    cps = (CardPane[][])map.get("cardPane");
                }

                Map<String, Object> sc = gs.getScoreAndQuantities();
                if (sc != null) {
                    usecount = ((Integer)sc.get("usecount")).intValue();
                    aiuse = ((Integer)sc.get("aiuse")).intValue();
                    step = ((Integer)sc.get("step")).intValue();
                    score = ((Long)sc.get("score")).longValue();
                    mcQuantities = (int[])sc.get("quantities");
                }

                record = gs.getRecord();

                resumeGridPane();


                if (aiuse!=0){
                    //用Timer等待1.5秒
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), ev -> {
                        Alert alert=new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(alert.getAlertType().toString());
                        alert.setContentText("本存档已使用过AI，得分清空！");
                        alert.show();
                    }));
                    timeline.play();


                }
            }catch(Exception ex) {
                ex.printStackTrace();
                //异常时新开游戏
                this.cols=4;
                this.rows=4;
                init();
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), ev -> {
                    Alert alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("存档读取异常，已重置游戏");
                    alert.show();
                }));
                timeline.play();

            }
        }
        getChildren().add(gridPane);
        gridPane.setMouseTransparent(true);
    }

    public CardMatrixPane(int cols, int rows, Game game) {//application供回调方法使用

        mCallbacks=(GameCallbacks)game;
        gs = new GameSaver(game.getUsername());
        this.cols=cols;
        this.rows=rows;
        this.game = game;
//     this.setBackground(new Background(new BackgroundFill(Color.BLUE,CornerRadii.EMPTY,Insets.EMPTY)));//测试用
        init();
        getChildren().add(gridPane);
        gridPane.setMouseTransparent(true);
    }

    /**
     * 获取卡片矩阵
     *
     * @return
     */
    public CardPane[][] getCardPane() {
        return cps;
    }

    /**
     * 获取卡片矩阵容器
     */
    public GridPane getGridPane() {
        return gridPane;
    }

    /**
     * 获取分数
     */
    public long getScore() {
        return score;
    }

    /**
     * 获取合并过的卡片数字数量
     */
    public int[] getMcQuantities() {
        return mcQuantities;
    }

    public void setUsecount(int usecount){
        this.usecount=usecount;
    }

    public int getUsecount(){
        return usecount;
    }

    public void setAiuse(int aiuse){
        this.aiuse=aiuse;
    }

    public int getAiuse(){
        return aiuse;
    }

    private void init() {
        initGridPane();//初始化GridPane
        createRandomNumber();//在随机的空卡片上生成数字,这个方法会返回布尔值,但这里不需要
    }

    /**
     * 初始化GridPane
     */
    private void initGridPane() {
        setUsecount(0);
        setAiuse(0);
        gridPane = new GridPane();
//		gridPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,CornerRadii.EMPTY,Insets.EMPTY)));//测试用
//		gridPane.setGridLinesVisible(true);//单元格边框可见,测试用

        //对this尺寸监听
        widthProperty().addListener(ov -> setGridSizeAndCardFont());//宽度变化,更新边长和字号
        heightProperty().addListener(ov -> setGridSizeAndCardFont());//高度变化,更新边长和字号
        //单元格间隙
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        //绘制每个单元格
        cps = new CardPane[cols][rows];
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = new CardPane(0);
                gridPane.add(card, i, j);
                cps[i][j] = card;

            }
        }
    }


    private void resumeGridPane() {

        gridPane = new GridPane();
//     gridPane.setBackground(new Background(new BackgroundFill(Color.YELLOW,CornerRadii.EMPTY,Insets.EMPTY)));//测试用
//     gridPane.setGridLinesVisible(true);//单元格边框可见,测试用

        //对this尺寸监听
        widthProperty().addListener(ov -> setGridSizeAndCardFont());//宽度变化,更新边长和字号
        heightProperty().addListener(ov -> setGridSizeAndCardFont());//高度变化,更新边长和字号
        //单元格间隙
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        //绘制每个单元格

        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                //CardPane card=new CardPane(0);
                gridPane.add(cps[i][j], i, j);
            }
        }
    }

    /**
     * 设置GridPane的边长,其内部单元格的尺寸和CardPane的字号
     */
    private void setGridSizeAndCardFont() {
        double w = widthProperty().get();
        double h = heightProperty().get();
        double min = Math.min(w, h);
        gridPane.setMaxWidth(min);
        gridPane.setMaxHeight(min);
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                card.getLabel().setFont(new Font((min / 14) / cols * 4));//设置显示数字的尺寸
                //由于下面两行代码主动设置了每个单元格内cardPane的尺寸,gridPane不需要自动扩张
                card.setPrefWidth(min - 5 * (cols - 1));//设置单元格内cardPane的宽度,否则它会随其内容变化,进而影响单元格宽度
                card.setPrefHeight(min - 5 * (rows - 1));//设置单元格内cardPane的高度,否则它会随其内容变化,进而影响单元格高度
            }
        }
    }

    public boolean beforeAction() {


        CardPane maxCard = getMaxCard();//最大卡片
        if (maxCard.getType() == 16) {//出现最大数字
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(alert.getAlertType().toString());
            alert.setContentText("恭喜你,游戏的最大数字为" + maxCard.getNumber() + ",可在菜单栏选择重新开始\n" +
                    "事实上,我们还尚未准备比" + maxCard.getNumber() + "更大的数字卡片,终点已至");
            alert.show();
            return false;
        } else return true;
    }
/*
无提示，AI专用
 */
    public boolean beforeAction2() {
        CardPane maxCard = getMaxCard();//最大卡片
        if (maxCard.getType() == 16) {//出现最大数字
            return false;
        } else return true;
    }

    /**
     * 添加键盘监听
     */
    public void createKeyListener() {

        requestFocus();
        setOnKeyPressed(e -> {//键盘按下事件
            KeyCode kc = e.getCode();

            // 如果是左右箭头键，阻止默认行为
            if (kc == KeyCode.LEFT || kc == KeyCode.RIGHT) {
                e.consume(); // 阻止事件传播，避免焦点切换
            }
            // 如果键已经处理，直接返回
            if (keyProcessed) {
                return;
            }

            keyProcessed = true; // 标记键已经处理
            if (!beforeAction()) return;//动作前

            switch (kc) {
                case UP:
                case W:

                    if(testUp()) {goUp();//↑
                    afterAction();
                    setStep(getStep()+1);}
                    if(isGameOver()){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle(alert.getAlertType().toString());
                        alert.setHeaderText(null);
                        alert.setContentText("游戏结束，本次最大数字为 " + getMaxCard().getNumber() + "，是否重新开始？");

                        ButtonType buttonTypeYes = new ButtonType("是");
                        ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonTypeYes) {
                            restartMatrix();
                        } else {
                            // Close the alert
                            alert.close();
                        }

                    }
                    break;
                case DOWN:
                case S:
                    if(testDown()){goDown();//↓
                    afterAction();
                    setStep(getStep()+1);}
                    if(isGameOver()){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle(alert.getAlertType().toString());
                        alert.setHeaderText(null);
                        alert.setContentText("游戏结束，本次最大数字为 " + getMaxCard().getNumber() + "，是否重新开始？");

                        ButtonType buttonTypeYes = new ButtonType("是");
                        ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonTypeYes) {
                            restartMatrix();
                        } else {
                            // Close the alert
                            alert.close();
                        }
                    }
                    break;
                case LEFT:
                case A:
                    if(testLeft()) {goLeft();//←
                    afterAction();
                    setStep(getStep()+1);}
                    if(isGameOver()){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle(alert.getAlertType().toString());
                        alert.setHeaderText(null);
                        alert.setContentText("游戏结束，本次最大数字为 " + getMaxCard().getNumber() + "，是否重新开始？");

                        ButtonType buttonTypeYes = new ButtonType("是");
                        ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonTypeYes) {
                            restartMatrix();
                        } else {
                            // Close the alert
                            alert.close();
                        }
                    }
                    break;
                case RIGHT:
                case D:
                    if(testRight()) {goRight();//→
                    afterAction();
                    setStep(getStep()+1);}
                    if(isGameOver()){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle(alert.getAlertType().toString());
                        alert.setHeaderText(null);
                        alert.setContentText("游戏结束，本次最大数字为 " + getMaxCard().getNumber() + "，是否重新开始？");

                        ButtonType buttonTypeYes = new ButtonType("是");
                        ButtonType buttonTypeNo = new ButtonType("否", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == buttonTypeYes) {
                            restartMatrix();
                        } else {
                            // Close the alert
                            alert.close();
                        }
                    }
                    break;
                default:
                    return;//未定义的操作
            }


        });
        setOnKeyReleased(e -> {
            // 重置标记，允许处理下一个键盘事件
            keyProcessed = false;
        });
    }




    public void afterAction() {
        CardPane maxCard = getMaxCard();//最大卡片
        if(getGame().getToolMenu().soundEnabled){
            playsound();
        }if (aiuse==0) {
            redrawAllCardsAndResetIsMergeAndSetScore();//重绘所有的卡片,并重设合并记录,更新分数:
        }else {
            redrawAllCards();
        }
        // 创建一个暂停0.07秒的动画
        PauseTransition pause = new PauseTransition(Duration.seconds(0.07));
        boolean[] testOpe = {false};//是否还能进行横向或竖向操作

        // 设置动画结束后的操作
        pause.setOnFinished(event -> {

//            boolean isFull = !
                    createRandomNumber();//生成新的随机数字卡片,并判满,这包含了生成数字后满的情况

//            if (isFull) {//矩阵已满,可能已经游戏结束
//
//                testOpe[0] |= testUp();//还能进行竖向操作
//                testOpe[0] |= testRight();//还能进行横向操作
//                testOpe[0] |= testLeft();//还能进行横向操作
//                testOpe[0] |= testDown();//还能进行竖向操作
//                if (!testOpe[0]) {//游戏结束
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle(alert.getAlertType().toString());
//                    alert.setContentText("游戏结束,本次最大数字为" + maxCard.getNumber() + ",可在菜单栏选择重新开始\n");
//                    alert.show();
//                }
//
//            }


        });


// 开始动画
        pause.play();
    }

    //无动画版本的afterAction，AI专用
    public void afterAction2() {
        aiuse=1;
        CardPane maxCard = getMaxCard();//最大卡片
        redrawAllCards();//重绘所有的卡片,并重设合并记录,更新分数:
        createRandomNumber2();
//        boolean[] testOpe = {true};//是否还能进行横向或竖向操作
//
//        boolean isFull = !createRandomNumber2();//生成新的随机数字卡片,并判满,这包含了生成数字后满的情况
//
//        if (isFull) {//矩阵已满,可能已经游戏结束
//            testOpe[0] = false;
//            testOpe[0] |= testUp();//还能进行竖向操作
//            testOpe[0] |= testRight();//还能进行横向操作
//            testOpe[0] |= testLeft();//还能进行横向操作
//            testOpe[0] |= testDown();//还能进行竖向操作
//        }
    }

    public boolean isGameOver(){
        boolean[] testOpe = {true};//是否还能进行横向或竖向操作

        boolean isFull = !isFull();//生成新的随机数字卡片,并判满,这包含了生成数字后满的情况


        if (isFull) {//矩阵已满,可能已经游戏结束
            testOpe[0] = false;
            testOpe[0] |= testUp();//还能进行竖向操作

            testOpe[0] |= testRight();//还能进行横向操作

            testOpe[0] |= testLeft();//还能进行横向操作

            testOpe[0] |= testDown();//还能进行竖向操作
        }

        return !testOpe[0];
    }




    /*
     * 以下是四个带动画的操作
     *
     *
     ************/

    /**
     * 向上操作
     */
    public void goUp() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
                for (int j = 1; j < rows; j++) {//从第二行起向下,遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j - 1];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        animateMove(card, i, j, i, j - 1); // 上移
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向下操作
     */
    public void goDown() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
                for (int j = rows - 2; j >= 0; j--) {//从倒数第二行起向上,遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j + 1];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        animateMove(card, i, j, i, j + 1);
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向左操作
     */
    public void goLeft() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 1; i < cols; i++) {//从第二列起向右,遍历卡片矩阵的列
                for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i - 1][j];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        animateMove(card, i, j, i - 1, j);
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向右操作
     */
    public void goRight() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = cols - 2; i >= 0; i--) {//从倒数第二列起向左,遍历卡片矩阵的列
                for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i + 1][j];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        animateMove(card, i, j, i + 1, j);
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /*
     * 以下是四个不带动画的操作
     *
     *
     ************/

    /**
     * 向上操作
     */
    public void goUp2() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
                for (int j = 1; j < rows; j++) {//从第二行起向下,遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j - 1];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向下操作
     */
    public void goDown2() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
                for (int j = rows - 2; j >= 0; j--) {//从倒数第二行起向上,遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i][j + 1];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向左操作
     */
    public void goLeft2() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = 1; i < cols; i++) {//从第二列起向右,遍历卡片矩阵的列
                for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i - 1][j];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }

    /**
     * 向右操作
     */
    public void goRight2() {
        boolean mergeOrMoveExist;//矩阵的这次操作的一次遍历中是否存在移动或合并
        do {
            mergeOrMoveExist = false;//初始为false
            for (int i = cols - 2; i >= 0; i--) {//从倒数第二列起向左,遍历卡片矩阵的列
                for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                    CardPane card = cps[i][j];
                    CardPane preCard = cps[i + 1][j];//前一个卡片
                    boolean isChanged = false;
                    if (card.canMergeOrMove(preCard)) {
                        isChanged = true;
                        card.tryMergeOrMoveInto(preCard);
                    }
                    mergeOrMoveExist |= isChanged;//只要有一次移动或合并记录,就记存在为true
                }
            }
        } while (mergeOrMoveExist);//如果存在移动或合并,就可能需要再次遍历,继续移动或合并
    }


    /**
     * 测试是否能向上操作
     */
    public boolean testUp() {
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 1; j < rows; j++) {//从第二行起向下,遍历卡片矩阵的行
                CardPane card = cps[i][j];
                CardPane preCard = cps[i][j - 1];//前一个卡片
                if (card.canMergeOrMove(preCard)) {
                    return true;//能
                }
            }
        }
        return false;//不能
    }

    /**
     * 测试是否能向下操作
     */
    public boolean testDown() {
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = rows - 2; j >= 0; j--) {//从倒数第二行起向上,遍历卡片矩阵的行
                CardPane card = cps[i][j];
                CardPane preCard = cps[i][j + 1];//前一个卡片
                if (card.canMergeOrMove(preCard)) {
                    return true;//能
                }
            }
        }
        return false;//不能
    }

    /**
     * 测试是否能向左操作
     */
    public boolean testLeft() {
        for (int i = 1; i < cols; i++) {//从第二列起向右,遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                CardPane preCard = cps[i - 1][j];//前一个卡片
                if (card.canMergeOrMove(preCard)) {
                    return true;//能
                }
            }
        }
        return false;//不能
    }

    /**
     * 测试是否能向右操作
     */
    public boolean testRight() {
        for (int i = cols - 2; i >= 0; i--) {//从倒数第二列起向左,遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                CardPane preCard = cps[i + 1][j];//前一个卡片
                if (card.canMergeOrMove(preCard)) {
                    return true;//能
                }
            }
        }
        return false;//不能
    }


    /**
     * 重绘所有的卡片,并重设合并记录,并设置分数
     */
    private void redrawAllCardsAndResetIsMergeAndSetScore() {

        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                card.draw();
                if (card.isMerge()) {//这张卡片合并过
                    score += card.getNumber();//计入分数
                    mcQuantities[card.getType() - 2]++;//相应的合并过的卡片数字数量+1
                    card.setMerge(false);
                }
            }
        }
        mCallbacks.afterScoreChange();
    }

    /**
     * AI用，重绘所有的卡片,并重设合并记录,不设置分数
     */
    private void redrawAllCards() {
        setAiuse(1);
        score=0;
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                card.draw();
                if (card.isMerge()) {//这张卡片合并过
                    mcQuantities[card.getType() - 2]++;//相应的合并过的卡片数字数量+1
                    card.setMerge(false);
                }
            }
        }
        mCallbacks.afterScoreChange();
    }


    public void reDraw(){
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                card.draw();
            }
        }
    }

    /**
     * 获取卡片矩阵中的最大卡片
     */
    public CardPane getMaxCard() {
        CardPane maxCard = new CardPane();//type=0的新卡片
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                if (card.getType() > maxCard.getType()) {
                    maxCard = card;
                }
            }
        }
        return maxCard;
    }

    /**
     * 在随机的空卡片上生成新的数字,若矩阵已满,或生成数字后满,则返回false
     */
    public boolean createRandomNumber() {
        List<CardPane> voidCards = new ArrayList<>();//空卡片列表

        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                if (card.getType() == 0) {//是空卡片
                    voidCards.add(card);//添加到列表中
                }
            }
        }
        int len = voidCards.size();
        if (len == 0) {//没有空卡片了,返回
            return false;//判满
        }

        int type;
        int index = (int) (Math.random() * 5);//0,1,2,3,4
        if (index != 0) {//4/5概率
            type = 1;//number=2
//			type=7;//number=128
        } else {//1/5概率
            type = 2;//number=4
//			type=8;//number=256
        }
        int voidCardIndex = (int) (Math.random() * len);
        CardPane card = voidCards.get(voidCardIndex);
        card.setType(type);//更新type,生成数字


        FadeTransition ft = new FadeTransition(Duration.millis(200), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);
        ft.play();
        card.draw();//重绘此卡片
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(70), card);
        scaleTransition.setToX(0.1f);
        scaleTransition.setToY(0.1f);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);

        scaleTransition.setOnFinished(event -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });

        scaleTransition.play();


        if (len == 1) {//只有一个空卡片,矩阵生成数字后满
            return false;
        }
        return true;
    }
    /*
    无动画版本
     */

    public boolean createRandomNumber2() {
        List<CardPane> voidCards = new ArrayList<>();//空卡片列表

        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                if (card.getType() == 0) {//是空卡片
                    voidCards.add(card);//添加到列表中
                }
            }
        }
        int len = voidCards.size();
        if (len == 0) {//没有空卡片了,返回
            return false;//判满
        }

        int type;
        int index = (int) (Math.random() * 5);//0,1,2,3,4
        if (index != 0) {//4/5概率
            type = 1;//number=2
//			type=7;//number=128
        } else {//1/5概率
            type = 2;//number=4
//			type=8;//number=256
        }
        int voidCardIndex = (int) (Math.random() * len);
        CardPane card = voidCards.get(voidCardIndex);
        card.setType(type);//更新type,生成数字
        card.draw2();//重绘此卡片
        if (len == 1) {//只有一个空卡片,矩阵生成数字后满
            return false;
        }
        return true;
    }

    /**
     *
     * 若矩阵已满则返回false
     */
    public boolean isFull() {
        List<CardPane> voidCards = new ArrayList<>();//空卡片列表

        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                if (card.getType() == 0) {//是空卡片
                    voidCards.add(card);//添加到列表中
                }
            }
        }
        int len = voidCards.size();
        //没有空卡片了,返回
        return len != 0;//判满
    }

    /**
     * 重启卡片矩阵,并在随机的空卡片上生成数字
     */
    public void restartMatrix() {
        try {
            gs.saveRecord(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<cols;i++) {//遍历卡片矩阵的列
            for(int j=0;j<rows;j++) {//遍历卡片矩阵的行
                CardPane card=cps[i][j];
                card.setType(0);
                card.draw();//重绘
            }
        }
        usecount=0;
        aiuse=0;
        step=0;
        score = 0;//重设分数
        mcQuantities = new int[15];//重设合并过的卡片数字数量
        mCallbacks.afterScoreChange();
        createRandomNumber();//在随机的空卡片上生成数字,这个方法会返回布尔值,但这里不需要
    }

    /**
     * 进行颜色测试,可在4*4矩阵中显示2至65536
     */
    public void testColors() {
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                int type = i * 4 + j + 1;
                if (type > 16) {
                    return;
                }
                card.setType(i * 4 + j + 1);
                card.draw();//重绘
            }
        }
    }

    /**
     * 卡片移动的动画
     */
    private void animateMove(CardPane card, int fromCol, int fromRow, int toCol, int toRow) {
        // 创建一个复制卡片
        CardPane copyCard = new CardPane(card.getType());
        copyCard.setLayoutX(card.getLayoutX());
        copyCard.setLayoutY(card.getLayoutY());
        gridPane.getChildren().add(copyCard);

        //更新copycard字号
        copyCard.getLabel().setFont(card.getLabel().getFont());

        // 计算水平和垂直方向上的位移
        double deltaX = (toCol - fromCol) * (card.getWidth() + gridPane.getHgap()) * 1;
        double deltaY = (toRow - fromRow) * (card.getHeight() + gridPane.getVgap()) * 1;

        // 设置复制卡片的初始位置为移动前卡片的位置
        copyCard.setTranslateX(card.getLayoutX());
        copyCard.setTranslateY(card.getLayoutY());

        // 创建平移动画
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.07), copyCard);
        tt.setByX(deltaX);
        tt.setByY(deltaY);
        tt.setOnFinished(event -> {
            // 动画结束后，更新GridPane中的卡片位置，并移除复制的卡片
            gridPane.getChildren().remove(copyCard);
        });
        tt.play();
    }

    public CardPane[][] getCps() {
        return cps;
    }

    public void setCps(CardPane[][] cps) {
        this.cps = cps;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public void setMcQuantities(int[] mcQuantities) {
        this.mcQuantities = mcQuantities;
    }


    @Override
    public CardMatrixPane clone() {
        CardMatrixPane clone = new CardMatrixPane(cols, rows, game);
        CardPane[][] cpsClone = new CardPane[cols][rows];
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                CardPane cardClone = new CardPane(card.getType());
                cpsClone[i][j] = cardClone;
            }
        }
        clone.setCps(cpsClone);
        return clone;
    }
    public String getBoardString(){
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (getCps()[i][j] != null) {
                    boardString.append(getCps()[i][j].getNumber());
                } else {
                    boardString.append("0");
                }
            }
        }
        return boardString.toString();
    }

    public List<String> getRecord() {
        return record;
    }

    public int getCols(){
        return cols;
    }

    public void soundeffect(int i){
        try {
            AudioInputStream audioInputStream1= AudioSystem.getAudioInputStream(new File("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\soundeffect2.wav"));
            AudioInputStream audioInputStream2= AudioSystem.getAudioInputStream(new File("C:\\Users\\zhuzh\\IdeaProjects\\Project\\src\\game\\sources\\soundeffect2.wav"));
            Clip clip = AudioSystem.getClip();
            if (i==1) {
                clip.open(audioInputStream1);
                clip.start();
            }
            if (i==2) {
                clip.open(audioInputStream2);
                clip.start();
            }
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }

    private void playsound() {
        int count  =0;
        for (int i = 0; i < cols; i++) {//遍历卡片矩阵的列
            for (int j = 0; j < rows; j++) {//遍历卡片矩阵的行
                CardPane card = cps[i][j];
                if (card.isMerge()){
                    count++;
                }
            }
        }
        if (count==0){
            soundeffect(1);
        }else{
            soundeffect(2);
        }
    }
}


