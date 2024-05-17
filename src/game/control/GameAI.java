package edu.sustech.game.control;

import edu.sustech.game.pane.CardMatrixPane;

public class GameAI {

    private final float smoothWeight = 1.0f;
    private  final float mono2Weight = 1.0f;
    private final  float emptyWeight = 1.0f;
    private  final float maxWeight = 1.0f;
    private CardMatrixPane cardMatrixPane;
    private int[][] cps;


    public GameAI(CardMatrixPane cardMatrixPane){
        this.cardMatrixPane = new CardMatrixPane(cardMatrixPane.getGame());
        this.cardMatrixPane.setCps(cardMatrixPane.getCps());



    }

    public value getBestMove(){

    }

    public enum value{
        UP, DOWN, LEFT, RIGHT


    }

    private float smoothness(){
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


    private float monotonicity(){
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

    private float emptyCells(){
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

    private float maxValue(){
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

    private float evaluate(){
        return smoothWeight * smoothness()
                + mono2Weight * monotonicity()
                + emptyWeight * emptyCells()
                + maxWeight * (float)Math.log(maxValue()) ;
    }

    private float minmax(int depth, float alpha, float beta, value direction){
        if(depth == 0){
           return evaluate();
        }
        if(direction == value.UP){
            cardMatrixPane.goUp();
            minmax(depth - 1, alpha, beta, value.LEFT);
            cardMatrixPane.goDown();
            minmax(depth - 1, alpha, beta, value.RIGHT);
        }else if(direction == value.DOWN){
            cardMatrixPane.goDown();
            minmax(depth - 1, alpha, beta, value.LEFT);
            cardMatrixPane.goUp();
            minmax(depth - 1, alpha, beta, value.RIGHT);
        }else if(direction == value.LEFT){
            cardMatrixPane.goLeft();
            minmax(depth - 1, alpha, beta, value.UP);
            cardMatrixPane.goRight();
            minmax(depth - 1, alpha, beta, value.DOWN);
        }else if(direction == value.RIGHT){
            cardMatrixPane.goRight();
            minmax(depth - 1, alpha, beta, value.UP);
            cardMatrixPane.goLeft();
            minmax(depth - 1, alpha, beta, value.DOWN);
        }
        return 0;
    }










    public void startGame(){
        value bestMove = getBestMove();
        if(bestMove == value.UP){
            cardMatrixPane.goUp();
        }else if(bestMove == value.DOWN){
            cardMatrixPane.goDown();
        }else if(bestMove == value.LEFT){
            cardMatrixPane.goLeft();
        }else if(bestMove == value.RIGHT){
            cardMatrixPane.goRight();
        }
    }





}
