package edu.sustech.game.control;

import edu.sustech.game.pane.CardMatrixPane;

import javax.naming.directory.SearchResult;
import java.util.*;

public class GameAI {

    private final float smoothWeight = 1.0f;
    private final float monoWeight = 1.3f;
    private final float emptyWeight = 2.7f;
    private final float maxWeight = 1.0f;
//    private Map<String, Integer> cache = new HashMap<>();

    private int[][] cps;

    public enum value {
        UP, DOWN, LEFT, RIGHT, NULL, UP_DOWN, LEFT_RIGHT, UP_LEFT, UP_RIGHT, LEFT_DOWN, RIGHT_DOWN, _RIGHT, _UP, _DOWN, _LEFT, ALL


    }


    private float smoothness(CardMatrixPane cardMatrixPane) {
        float smoothness = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (cardMatrixPane.getCps()[x][y] != null) {
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    int[] values = new int[4];
                    int index = 0;
                    for (int i = 0; i < 4; i++) {
                        if (cardMatrixPane.getCps()[x][i] != null) {
                            values[index] = cardMatrixPane.getCps()[x][i].getNumber();
                            index++;
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        if (values[i] != 0) {
                            if (values[i] == values[i + 1]) {
                                smoothness += 1;
                            }
                        }
                    }
                }
            }
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (cardMatrixPane.getCps()[x][y] != null) {
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    int[] values = new int[4];
                    int index = 0;
                    for (int i = 0; i < 4; i++) {
                        if (cardMatrixPane.getCps()[i][y] != null) {
                            values[index] = cardMatrixPane.getCps()[i][y].getNumber();
                            index++;
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        if (values[i] != 0) {
                            if (values[i] == values[i + 1]) {
                                smoothness += 1;
                            }
                        }
                    }
                }
            }
        }
        return smoothness;
    }


    private float monotonicity(CardMatrixPane cardMatrixPane) {
        int[] totals = new int[4];
        for (int x = 0; x < 4; x++) {
            int current = 0;
            int next = current + 1;
            while (next < 4) {
                while (next < 4 && cardMatrixPane.getCps()[x][next] == null) {
                    next++;
                }
                if (next >= 4) {
                    next--;
                }
                int currentValue = cardMatrixPane.getCps()[x][current] == null ? 0 : cardMatrixPane.getCps()[x][current].getNumber();
                int nextValue = cardMatrixPane.getCps()[x][next] == null ? 0 : cardMatrixPane.getCps()[x][next].getNumber();
                if (currentValue > nextValue) {
                    totals[0] += nextValue - currentValue;
                } else if (nextValue > currentValue) {
                    totals[1] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }
        for (int y = 0; y < 4; y++) {
            int current = 0;
            int next = current + 1;
            while (next < 4) {
                while (next < 4 && cardMatrixPane.getCps()[next][y] == null) {
                    next++;
                }
                if (next >= 4) {
                    next--;
                }
                int currentValue = cardMatrixPane.getCps()[current][y] == null ? 0 : cardMatrixPane.getCps()[current][y].getNumber();
                int nextValue = cardMatrixPane.getCps()[next][y] == null ? 0 : cardMatrixPane.getCps()[next][y].getNumber();
                if (currentValue > nextValue) {
                    totals[2] += nextValue - currentValue;
                } else if (nextValue > currentValue) {
                    totals[3] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }
        return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
    }

    private float emptyCells(CardMatrixPane cardMatrixPane) {
        float emptyCells = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (cardMatrixPane.getCps()[x][y] == null) {
                    emptyCells++;
                }
            }
        }
        return emptyCells;
    }

    private float maxValue(CardMatrixPane cardMatrixPane) {
        int max = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (cardMatrixPane.getCps()[x][y] != null) {
                    int value = cardMatrixPane.getCps()[x][y].getNumber();
                    if (value > max) {
                        max = value;
                    }
                }
            }
        }
        return max;
    }

    private float evaluate(CardMatrixPane cardMatrixPane) {
        if (cardMatrixPane.isGameOver()) {
            return -0.1f;
        } else {
            return smoothWeight * smoothness(cardMatrixPane)
                    + monoWeight * monotonicity(cardMatrixPane)
                    + emptyWeight * emptyCells(cardMatrixPane)
                    + maxWeight * (float) Math.log(maxValue(cardMatrixPane));
        }
    }


    //这是minmax算法
    private void minmax(CardMatrixPane cardMatrixPaneOrigin, int depth, int[] max, value direction) {

        CardMatrixPane cardMatrixPane = cardMatrixPaneOrigin.clone();
        if (depth == 0) {
            max[0] = Math.max(max[0], EvaluationFunction.evaluate(cardMatrixPane));

            return;
        }
        if (direction == value.UP) {
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testUp()) {
                cardMatrixPane.goUp2();
                cardMatrixPane.afterAction2();
                String boardState1 = cardMatrixPane.getBoardString();


            }
            minmax(cardMatrixPane, depth - 1, max, value.LEFT);
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testDown()) {
                cardMatrixPane.goDown2();
                cardMatrixPane.afterAction2();
                String boardState1 = cardMatrixPane.getBoardString();

            }
            minmax(cardMatrixPane, depth - 1, max, value.RIGHT);
        } else if (direction == value.DOWN) {
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testDown()) {
                cardMatrixPane.goDown2();
                cardMatrixPane.afterAction2();


            }
            minmax(cardMatrixPane, depth - 1, max, value.LEFT);
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testUp()) {
                cardMatrixPane.goUp2();
                cardMatrixPane.afterAction2();


            }
            minmax(cardMatrixPane, depth - 1, max, value.RIGHT);
        } else if (direction == value.LEFT) {
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testLeft()) {
                cardMatrixPane.goLeft2();
                cardMatrixPane.afterAction2();


            }
            minmax(cardMatrixPane, depth - 1, max, value.UP);
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testRight()) {
                cardMatrixPane.goRight2();
                cardMatrixPane.afterAction2();

            }
            minmax(cardMatrixPane, depth - 1, max, value.DOWN);
        } else if (direction == value.RIGHT) {
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testRight()) {
                cardMatrixPane.goRight2();
                cardMatrixPane.afterAction2();

            }
            minmax(cardMatrixPane, depth - 1, max, value.UP);
            cardMatrixPane.beforeAction2();
            if (cardMatrixPane.testLeft()) {
                cardMatrixPane.goLeft2();
                cardMatrixPane.afterAction2();

            }
            minmax(cardMatrixPane, depth - 1, max, value.DOWN);
        }
    }


    //这是mimmax算法的findBestMove
    public value findBestMove(CardMatrixPane cardMatrixPane) {

        float tempMaxUP = 0.00f;
        for (int i = 0; i < 5; i++) {
            int[] maxUP = new int[1];
            maxUP[0] = 0;
            minmax(cardMatrixPane, 6, maxUP, value.UP);
            tempMaxUP += maxUP[0];
        }
        float MaxUP = tempMaxUP / 5;

        float tempMaxDOWN = 0.00f;
        for (int i = 0; i < 5; i++) {
            int[] maxDOWN = new int[1];
            maxDOWN[0] = 0;
            minmax(cardMatrixPane, 6, maxDOWN, value.DOWN);
            tempMaxDOWN += maxDOWN[0];
        }
        float MaxDOWN = tempMaxDOWN / 5;

        float tempMaxLEFT = 0.00f;
        for (int i = 0; i < 5; i++) {
            int[] maxLEFT = new int[1];
            maxLEFT[0] = 0;
            minmax(cardMatrixPane, 6, maxLEFT, value.LEFT);
            tempMaxLEFT += maxLEFT[0];
        }
        float MaxLEFT = tempMaxLEFT / 5;

        float tempMaxRIGHT = 0.00f;
        for (int i = 0; i < 5; i++) {
            int[] maxRIGHT = new int[1];
            maxRIGHT[0] = 0;
            minmax(cardMatrixPane, 6, maxRIGHT, value.RIGHT);
            tempMaxRIGHT += maxRIGHT[0];
        }
        float MaxRIGHT = tempMaxRIGHT / 5;

        if (MaxUP > MaxDOWN && MaxUP > MaxLEFT && MaxUP > MaxRIGHT) {
            return value.UP;
        }
        if (MaxDOWN > MaxUP && MaxDOWN > MaxLEFT && MaxDOWN > MaxRIGHT) {
            return value.DOWN;
        }
        if (MaxLEFT > MaxUP && MaxLEFT > MaxDOWN && MaxLEFT > MaxRIGHT) {
            return value.LEFT;
        }
        if (MaxRIGHT > MaxUP && MaxRIGHT > MaxDOWN && MaxRIGHT > MaxLEFT) {
            return value.RIGHT;
        }
        if (MaxUP == MaxDOWN && MaxUP > MaxLEFT && MaxUP > MaxRIGHT) {
            return value.UP_DOWN;
        }
        if (MaxUP == MaxLEFT && MaxUP > MaxDOWN && MaxUP > MaxRIGHT) {
            return value.UP_LEFT;
        }
        if (MaxUP == MaxRIGHT && MaxUP > MaxDOWN && MaxUP > MaxLEFT) {
            return value.UP_RIGHT;
        }
        if (MaxDOWN == MaxLEFT && MaxDOWN > MaxUP && MaxDOWN > MaxRIGHT) {
            return value.LEFT_DOWN;
        }
        if (MaxDOWN == MaxRIGHT && MaxDOWN > MaxUP && MaxDOWN > MaxLEFT) {
            return value.RIGHT_DOWN;
        }
        if (MaxLEFT == MaxRIGHT && MaxLEFT > MaxUP && MaxLEFT > MaxDOWN) {
            return value.LEFT_RIGHT;
        }
        if (MaxUP == MaxDOWN && MaxUP == MaxLEFT && MaxUP > MaxRIGHT) {
            return value._RIGHT;
        }
        if (MaxUP == MaxDOWN && MaxUP == MaxRIGHT && MaxUP > MaxLEFT) {
            return value._LEFT;
        }
        if (MaxUP == MaxLEFT && MaxUP == MaxRIGHT && MaxUP > MaxDOWN) {
            return value._DOWN;
        }
        if (MaxDOWN == MaxLEFT && MaxDOWN == MaxRIGHT && MaxDOWN > MaxUP) {
            return value._UP;
        }


        return value.NULL;
    }


    public void move(CardMatrixPane cardMatrixPane) {
        value bestMove = findBestMove(cardMatrixPane);
        if (bestMove == value.UP) {
            if (cardMatrixPane.testUp()) {
                cardMatrixPane.goUp2();
                cardMatrixPane.afterAction2();
            }
        }
        else if (bestMove == value.DOWN) {
            if (cardMatrixPane.testDown()) {
                cardMatrixPane.goDown2();
                cardMatrixPane.afterAction2();
            }
        }
        else if (bestMove == value.LEFT) {
            if (cardMatrixPane.testLeft()) {
                cardMatrixPane.goLeft2();
                cardMatrixPane.afterAction2();
            }
        }
        else if (bestMove == value.RIGHT) {
            if (cardMatrixPane.testRight()) {
                cardMatrixPane.goRight2();
                cardMatrixPane.afterAction2();
            }
        }
        else if (bestMove == value.LEFT_DOWN) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testDown()) {
                        cardMatrixPane.goDown2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testLeft()) {
                        cardMatrixPane.goLeft2();
                        cardMatrixPane.afterAction2();
                    }
                }

            }

        }
        else if (bestMove == value.LEFT_RIGHT) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testRight()) {
                        cardMatrixPane.goRight2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testLeft()) {
                        cardMatrixPane.goLeft2();
                        cardMatrixPane.afterAction2();
                    }
                }

            }

        }
        else if (bestMove == value.UP_DOWN) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testDown()) {
                        cardMatrixPane.goDown2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testUp()) {
                        cardMatrixPane.goUp2();
                        cardMatrixPane.afterAction2();
                    }
                }

            }

        }
        else if (bestMove == value.UP_LEFT) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testLeft()) {
                        cardMatrixPane.goLeft2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testUp()) {
                        cardMatrixPane.goUp2();
                        cardMatrixPane.afterAction2();
                    }
                }

            }

        }
        else if (bestMove == value.UP_RIGHT) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testRight()) {
                        cardMatrixPane.goRight2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testUp()) {
                        cardMatrixPane.goUp2();
                        cardMatrixPane.afterAction2();
                    }

                }
            }
        }
        else if (bestMove == value.RIGHT_DOWN) {
            //50%概率
            Random random = new Random();
            int i = random.nextInt(2);
            if (i == 0) {
                if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testDown()) {
                        cardMatrixPane.goDown2();
                        cardMatrixPane.afterAction2();
                    }
                }
            } else {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else {
                    if (cardMatrixPane.testRight()) {
                        cardMatrixPane.goRight2();
                        cardMatrixPane.afterAction2();
                    }
                }
            }
        }
        else if (bestMove == value._UP) {
            //三分之一概率
            Random random = new Random();
            int i = random.nextInt(3);
            if (i == 0) {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 1) {

                 if(cardMatrixPane.testLeft()){
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }
                else if(cardMatrixPane.testDown()){
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                }
                else if(cardMatrixPane.testRight()){
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 2) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }
                else if(cardMatrixPane.testDown()){
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                }
                else if(cardMatrixPane.testRight()){
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                }
            }

        }
        else if (bestMove == value._DOWN) {
            //三分之一概率
            Random random = new Random();
            int i = random.nextInt(3);
            if (i == 0) {

            } else if (i == 1) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 2) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }
            }

        }
        else if (bestMove == value._LEFT) {
            //三分之一概率
            Random random = new Random();
            int i = random.nextInt(3);
            if (i == 0) {
                if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 1) {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 2) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                }
            }

        }
        else if (bestMove == value._RIGHT) {
            //三分之一概率
            Random random = new Random();
            int i = random.nextInt(3);
            if (i == 0) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                }

            } else if (i == 1) {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 2) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }
            }

        }
        else if (bestMove == value.ALL) {
            //四分之一概率
            Random random = new Random();
            int i = random.nextInt(4);
            if (i == 0) {
                if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 1) {
                if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 2) {
                if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                }
            } else if (i == 3) {
                if (cardMatrixPane.testRight()) {
                    cardMatrixPane.goRight2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testUp()) {
                    cardMatrixPane.goUp2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testDown()) {
                    cardMatrixPane.goDown2();
                    cardMatrixPane.afterAction2();
                } else if (cardMatrixPane.testLeft()) {
                    cardMatrixPane.goLeft2();
                    cardMatrixPane.afterAction2();
                }

            }


        }
    }


}

class EvaluationFunction {

    // 三种阵型的权值矩阵
    private static final int[][][] weightMatrices = {
            {
                    {16, 15, 14, 13},
                    {9, 10, 11, 12},
                    {8, 7, 6, 5,},
                    {1, 2, 3, 4},
            },
            {
                    {16, 15, 12, 4},
                    {14, 13, 11, 3},
                    {10, 9, 8, 2},
                    {7, 6, 5, 1},

            },
            {
                    {16, 15, 14, 4},
                    {13, 12, 11, 3},
                    {10, 9, 8, 2},
                    {7, 6, 5, 1},
            }
    };

    public static int evaluate(CardMatrixPane cardMatrixPane) {
        int[][] board = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (cardMatrixPane.getCps()[i][j] != null) {
                    board[i][j] = cardMatrixPane.getCps()[i][j].getNumber();
                } else {
                    board[i][j] = 0;
                }
            }
        }
        int maxScore = Integer.MIN_VALUE;
        // 遍历每种阵型
        for (int[][] weightMatrix : weightMatrices) {
            // 对局面进行旋转和翻转得到8个方向
            for (int k = 0; k < 4; k++) {
                int score = calculateDirectionScore(board, weightMatrix);
                maxScore = Math.max(maxScore, score);
                board = rotateAndFlip(board);
            }
        }

        return maxScore;
    }

    // 计算某个方向上的得分
    private static int calculateDirectionScore(int[][] board, int[][] weightMatrix) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                score += board[i][j] * weightMatrix[i][j];
            }
        }
        return score;
    }

    // 将局面进行旋转和翻转
    private static int[][] rotateAndFlip(int[][] board) {
        int[][] result = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = board[3 - j][i];
            }
        }
        return result;
    }
}



