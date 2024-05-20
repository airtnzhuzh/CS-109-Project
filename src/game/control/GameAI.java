package edu.sustech.game.control;

import edu.sustech.game.pane.CardMatrixPane;
import edu.sustech.game.pane.CardPane;

import java.util.Arrays;

public class GameAI {

    private final float smoothWeight = 0.1f;
    private  final float monoWeight = 1.0f;
    private final  float emptyWeight = 3.0f;
    private  final float maxWeight = 2.0f;

    private int[][] cps;

    public enum value{
        UP, DOWN, LEFT, RIGHT,NULL


    }



    private float smoothness(CardMatrixPane cardMatrixPane){
        float smoothness = 0;
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                if(cardMatrixPane.getCps()[x][y] != null){
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    int[] values = new int[4];
                    int index = 0;
                    for(int i = 0; i < 4; i++){
                        if(cardMatrixPane.getCps()[x][i] != null){
                            values[index] = cardMatrixPane.getCps()[x][i].getNumber();
                            index++;
                        }
                    }
                    for(int i = 0; i < 3; i++){
                        if(values[i] != 0){
                            if(values[i] == values[i + 1]){
                                smoothness += 1;
                            }
                        }
                    }
                }
            }
        }
        for(int y = 0; y < 4; y++){
            for(int x = 0; x < 4; x++){
                if(cardMatrixPane.getCps()[x][y] != null){
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    int[] values = new int[4];
                    int index = 0;
                    for(int i = 0; i < 4; i++){
                        if(cardMatrixPane.getCps()[i][y] != null){
                            values[index] = cardMatrixPane.getCps()[i][y].getNumber();
                            index++;
                        }
                    }
                    for(int i = 0; i < 3; i++){
                        if(values[i] != 0){
                            if(values[i] == values[i + 1]){
                                smoothness += 1;
                            }
                        }
                    }
                }
            }
        }
        return smoothness;
    }


    private float monotonicity(CardMatrixPane cardMatrixPane){
        int[] totals = new int[4];
        for(int x = 0; x < 4; x++){
            int current = 0;
            int next = current + 1;
            while(next < 4){
                while(next < 4 && cardMatrixPane.getCps()[x][next] == null){
                    next++;
                }
                if(next >= 4){
                    next--;
                }
                int currentValue = cardMatrixPane.getCps()[x][current] == null ? 0 : cardMatrixPane.getCps()[x][current].getNumber();
                int nextValue = cardMatrixPane.getCps()[x][next] == null ? 0 : cardMatrixPane.getCps()[x][next].getNumber();
                if(currentValue > nextValue){
                    totals[0] += nextValue - currentValue;
                }else if(nextValue > currentValue){
                    totals[1] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }
        for(int y = 0; y < 4; y++){
            int current = 0;
            int next = current + 1;
            while(next < 4){
                while(next < 4 && cardMatrixPane.getCps()[next][y] == null){
                    next++;
                }
                if(next >= 4){
                    next--;
                }
                int currentValue = cardMatrixPane.getCps()[current][y] == null ? 0 : cardMatrixPane.getCps()[current][y].getNumber();
                int nextValue = cardMatrixPane.getCps()[next][y] == null ? 0 : cardMatrixPane.getCps()[next][y].getNumber();
                if(currentValue > nextValue){
                    totals[2] += nextValue - currentValue;
                }else if(nextValue > currentValue){
                    totals[3] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }
        return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
    }

    private float emptyCells(CardMatrixPane cardMatrixPane){
        float emptyCells = 0;
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                if(cardMatrixPane.getCps()[x][y] == null){
                    emptyCells++;
                }
            }
        }
        return emptyCells;
    }

    private float maxValue(CardMatrixPane cardMatrixPane){
        int max = 0;
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 4; y++){
                if(cardMatrixPane.getCps()[x][y] != null){
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    if(value > max){
                        max = value;
                    }
                }
            }
        }
        return max;
    }

    private float evaluate(CardMatrixPane cardMatrixPane){
        if(cardMatrixPane.afterAction2()){
            return -0.1f;
        }
        else {
            return smoothWeight * smoothness(cardMatrixPane)
                    + monoWeight * monotonicity(cardMatrixPane)
                    + emptyWeight * emptyCells(cardMatrixPane)
                    + maxWeight * (float)Math.log(maxValue(cardMatrixPane)) ;
        }
    }

    private void minmax(CardMatrixPane cardMatrixPaneOrigin,int depth, float[] max , value direction){
        CardMatrixPane cardMatrixPane = cardMatrixPaneOrigin.clone();
        if(depth == 0){
            max[0] = Math.max(max[0], evaluate(cardMatrixPane));
            return;
        }
        if(direction == value.UP){
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goUp2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1,max, value.LEFT);
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goDown2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1,max, value.RIGHT);
        }else if(direction == value.DOWN){
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goDown2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1, max, value.LEFT);
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goUp2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1, max, value.RIGHT);
        }else if(direction == value.LEFT){
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goLeft2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1,max, value.UP);
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goRight2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1, max, value.DOWN);
        }else if(direction == value.RIGHT){
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goRight2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1, max, value.UP);
            cardMatrixPane.beforeAction2();
            cardMatrixPane.goLeft2();
            cardMatrixPane.afterAction2();
            minmax(cardMatrixPane,depth - 1, max, value.DOWN);
        }
    }

    public value findBestMove(CardMatrixPane cardMatrixPane) {
        float[] maxUP = new float[1];
        maxUP[0] = 0;
        float[] maxDOWN = new float[1];
        maxDOWN[0] = 0;
        float[] maxLEFT = new float[1];
        maxLEFT[0] = 0;
        float[] maxRIGHT = new float[1];
        maxRIGHT[0] = 0;
        minmax(cardMatrixPane, 7, maxUP, value.UP);
        minmax(cardMatrixPane, 7, maxDOWN, value.DOWN);
        minmax(cardMatrixPane, 7, maxLEFT, value.LEFT);
        minmax(cardMatrixPane, 7, maxRIGHT, value.RIGHT);
        if (maxUP[0] > maxDOWN[0] && maxUP[0] > maxLEFT[0] && maxUP[0] > maxRIGHT[0]) {
            return value.UP;
        }
        if (maxDOWN[0] > maxUP[0] && maxDOWN[0] > maxLEFT[0] && maxDOWN[0] >maxRIGHT[0]) {
            return value.DOWN;
        }
        if (maxLEFT[0] > maxUP[0] && maxLEFT[0] > maxDOWN[0] && maxLEFT[0] > maxRIGHT[0]) {
            return value.LEFT;
        }
        if (maxRIGHT[0] > maxUP[0] && maxRIGHT[0] > maxDOWN[0] && maxRIGHT[0] > maxLEFT[0]) {
            return value.RIGHT;
        }
        if(maxUP[0] == maxDOWN[0] && maxUP[0] > maxLEFT[0] && maxUP[0] > maxRIGHT[0]){
            //二分之一概率随机
            int random = (int)(Math.random() * 2);
            if(random == 0) return value.UP;
            if(random == 1) return value.DOWN;
        }
        if(maxUP[0] == maxLEFT[0] && maxUP[0] > maxDOWN[0] && maxUP[0] > maxRIGHT[0]){
            //二分之一概率随机
            int random = (int)(Math.random() * 2);
            if(random == 0) return value.UP;
            if(random == 1) return value.LEFT;
        }
        if(maxUP[0] == 0 && maxDOWN[0] == 0 && maxLEFT[0] == 0 && maxRIGHT[0] == 0){
            //四分之一概率随机
            int random = (int)(Math.random() * 4);
            if(random == 0) return value.UP;
            if(random == 1) return value.DOWN;
            if(random == 2) return value.LEFT;
            if(random == 3) return value.RIGHT;
        }
        return value.NULL;


    }

    public void move(CardMatrixPane cardMatrixPane) {
        value bestMove = findBestMove(cardMatrixPane);
        if (bestMove == value.UP) {
            cardMatrixPane.goUp2();
        } else if (bestMove == value.DOWN) {
            cardMatrixPane.goDown2();
        } else if (bestMove == value.LEFT) {
            cardMatrixPane.goLeft2();
        } else if(bestMove == value.RIGHT) {
            cardMatrixPane.goRight2();
        }

    }

    public void movetest(CardMatrixPane cardMatrixPane) {
        findBestMove(cardMatrixPane);
        cardMatrixPane.goUp();
    }














}
