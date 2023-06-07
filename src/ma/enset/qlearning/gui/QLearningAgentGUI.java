package ma.enset.qlearning.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QLearningAgentGUI extends Application {

    private final int GRID_SIZE = 6;
    private final int ACTIONS_SIZE = 4;

    private int[][] grid;
    private double[][] qTable = new double[GRID_SIZE * GRID_SIZE][ACTIONS_SIZE];
    private int[][] actions;
    private int stateI;
    private int stateJ;

    private Button[][] buttons;
    private GridPane gridPane;

    private final double ALPHA = 0.1;
    private final double GAMMA = 0.9;
    private final double EPS = 0.4;
    private final int MAX_EPOCH = 200000;

    @Override
    public void start(Stage primaryStage) {
        initializeGrid();
        initializeActions();

        buttons = new Button[GRID_SIZE][GRID_SIZE];
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);
                button.setStyle("-fx-font-size: 14px;");
                gridPane.add(button, j, i);
                buttons[i][j] = button;
            }
        }

        trainAgent();

        showResult();



        Scene scene = new Scene(gridPane, 300, 300);
        primaryStage.setTitle("QL Agent Trajectory");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initializeGrid() {
        grid = new int[][]{
                { 0 , 0 , 0 , -1 , 0 , 0},
                {-1 , 0 ,  0 , 0 , -1 , 0},
                {0  , 0 , -1 , 0 , 0 , -1},
                {0  , -1 ,  1 , -1 , 0 , 0},
                {0  , 0 ,  0 , 0 , -1 , 0},
                {-1  , 0 ,  -1 , 0 , 0 , 0},
        };
    }

    public void initializeActions() {
        actions = new int[][]{
                {0, -1}, // Left
                {0, 1}, // Right
                {1, 0}, // Bottom
                {-1, 0} // Top
        };
    }

    public void trainAgent() {
        int it = 0;
        int currentState;
        int nextState;

        while (it < MAX_EPOCH) {
            resetState();
            while (!finished()) {
                currentState = stateI * GRID_SIZE + stateJ;
                int act = chooseAction(EPS);
                nextState = executeAction(act);
                int nextBestAct = chooseAction(0);

                // Bellman equation
                qTable[currentState][act] = qTable[currentState][act] + ALPHA * (grid[stateI][stateJ] + GAMMA * qTable[nextState][nextBestAct] - qTable[currentState][act]);

                it++;
            }
        }
    }

    public void showResult() {
        System.out.println("---------------- Q Table ----------------");
        for (double[] line : qTable) {
            System.out.println(Arrays.toString(line));
        }

        resetState();
        System.out.println("-------- Agent Road To Target ---------");

        int i = 0;
        while (!finished()) {
            int act = chooseAction(0);
            System.out.println("state : " + (stateI * GRID_SIZE + stateJ) + " -> action : " + directions(act) + "(" + act + ")");

            buttons[stateI][stateJ].setStyle("-fx-background-color: blue;");

            executeAction(act);
            i++;
        }

        System.out.println("final state : " + (stateI * GRID_SIZE + stateJ));
        System.out.println("transactions number : " + i);
    }

    public boolean finished() {
        return grid[stateI][stateJ] == 1;
    }

    public String directions(int i) {
        switch (i) {
            case 0:
                return "LEFT";
            case 1:
                return "RIGHT";
            case 2:
                return "BOTTOM";
            case 3:
                return "TOP";
            default:
                return " ";
        }
    }

    public void resetState() {
        stateI = 0;
        stateJ = 0;
    }

    public int chooseAction(double eps) {
        Random random = new Random();
        double bestQ = 0;
        int act = 0;
        if (random.nextDouble() < eps) {
            // Exploration
            act = random.nextInt(ACTIONS_SIZE);
        } else {
            // Exploitation
            int st = stateI * GRID_SIZE + stateJ;
            for (int i = 0; i < ACTIONS_SIZE; i++) {
                if (qTable[st][i] > bestQ) {
                    bestQ = qTable[st][i];
                    act = i;
                }
            }
        }
        return act;
    }

    public int executeAction(int act) {
        stateI = Math.max(0, Math.min(actions[act][0] + stateI, GRID_SIZE - 1));
        stateJ = Math.max(0, Math.min(actions[act][1] + stateJ, GRID_SIZE - 1));
        buttons[stateI][stateJ].setText(String.valueOf(stateI * GRID_SIZE + stateJ));

        // Update GUI with agent's position and color
        if (grid[stateI][stateJ] == 1) {
            buttons[stateI][stateJ].setStyle("-fx-background-color: green;");
        } else if (grid[stateI][stateJ] == -1) {
            buttons[stateI][stateJ].setStyle("-fx-background-color: red;");
        }

        return stateI * GRID_SIZE + stateJ;
    }

}
