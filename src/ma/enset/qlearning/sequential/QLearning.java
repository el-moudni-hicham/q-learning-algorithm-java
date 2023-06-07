package ma.enset.qlearning.sequential;

import java.util.Arrays;
import java.util.Random;

public class QLearning {

    // Q Learning Utils
    private final double ALPHA = 0.1;
    private final double GAMMA = 0.9;
    private final double EPS = 0.4;
    private final int MAX_EPOCH = 200000;
    private final int GRID_SIZE = 6;
    private final int ACTIONS_SIZE = 4;

    private int[][] grid;
    private double[][] qTable = new double[GRID_SIZE*GRID_SIZE][ACTIONS_SIZE];
    private int[][] actions;
    private int stateI;
    private int stateJ;

    public QLearning() {
        actions = new int[][]{
                {0,-1}, //Left
                {0,1}, //Right
                {1,0}, //Bottom
                {-1,0} //Top
        };

        grid = new int[][]{
                { 0 , 0 , 0 , -1 , 0 , 0},
                {-1 , 0 ,  0 , 0 , -1 , 0},
                {0  , 0 , -1 , 0 , 0 , -1},
                {0  , -1 ,  1 , -1 , 0 , 0},
                {0  , 0 ,  0 , 0 , -1 , 0},
                {-1  , 0 ,  -1 , 0 , 0 , 0},
        };
    }

    public void run(){
        int it = 0;

        int currentState;
        int nextState;

        while (it< MAX_EPOCH){
            resetState();
            while (!finished()){
                currentState = stateI*GRID_SIZE + stateJ;
                int act = chooseAction(EPS);
                nextState = executeAction(act);

                int nextBestAct = chooseAction(0);

                // Bellman equation
                qTable[currentState][act] = qTable[currentState][act] + ALPHA * (grid[stateI][stateJ] + GAMMA * qTable[nextState][nextBestAct] - qTable[currentState][act]);

                it++;
            }
        }
        showResult();
    }

    private void showResult(){
        System.out.println("---------------- Q Table ----------------");
        for (double[] line:qTable) {
            System.out.println(Arrays.toString(line));
        }

        resetState();
        System.out.println("-------- Agent Road To Target ---------");

        while (!finished()){
            int act = chooseAction(0);
            System.out.println("state : " + (stateI * GRID_SIZE + stateJ) + " -> action : " + derections(act) + "("+act+")");
            executeAction(act);
        }

        System.out.println("final state : " + (stateI * GRID_SIZE + stateJ));
    }

    private boolean finished(){
        return grid[stateI][stateJ] == 1;
    }

    private String derections(int i){
        switch (i){
            case 0 : return "LEFT";
            case 1 : return "RIGHT";
            case 2 : return "BOTTOM";
            case 3 : return "TOP";
            default: return " ";
        }
    }
    private void resetState(){
        stateI = 0;
        stateJ = 0;
    }

    private int chooseAction(double eps){
        Random random = new Random();
        double bestQ = 0;
        int act = 0;
        if(random.nextDouble() < eps){
            // Exploration
            act = random.nextInt(ACTIONS_SIZE);
        }else{
            // Exploiatation
            int st = stateI*GRID_SIZE + stateJ;
            for (int i = 0; i < ACTIONS_SIZE; i++) {
                if(qTable[st][i] > bestQ){
                    bestQ = qTable[st][i];
                    act = i;
                }
            }
        }
        return act;
    }

    private int executeAction(int act){
        stateI = Math.max(0, Math.min(actions[act][0]+stateI, GRID_SIZE-1));
        stateJ = Math.max(0, Math.min(actions[act][1]+stateJ, GRID_SIZE-1));
        return stateI*GRID_SIZE + stateJ;
    }
}
