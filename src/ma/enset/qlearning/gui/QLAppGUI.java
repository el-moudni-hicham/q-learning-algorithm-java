package ma.enset.qlearning.gui;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;

public class QLAppGUI extends Application {

    private int[][] grid;
    private double[][] qTable;
    private int[][] actions;
    private int stateI;
    private int stateJ;

    private Button[][] buttons;
    private GridPane gridPane;

    private double alpha;
    private double gamma;
    private double epsilon;
    private int gridSize;
    private int maxEpoch;

    @Override
    public void start(Stage primaryStage) {
        readUserInput();
        initializeActions();

        buttons = new Button[gridSize][gridSize];
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button button = new Button();
                button.setPrefSize(50, 50);
                button.setStyle("-fx-font-size: 14px;");
                gridPane.add(button, j, i);
                buttons[i][j] = button;
            }
        }

        trainAgent();

        showParameters();
        //showQTable();
        showRoadToTarget();

        Scene scene = new Scene(gridPane, 300, 300);
        primaryStage.setTitle("QL Agent Trajectory");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void readUserInput() {
        alpha = readDoubleFromUser("Enter alpha:");
        gamma = readDoubleFromUser("Enter gamma:");
        epsilon = readDoubleFromUser("Enter epsilon:");
        gridSize = readIntFromUser("Enter grid size:");
        maxEpoch = readIntFromUser("Enter max epoch:");

        initializeGrid();
    }

    public double readDoubleFromUser(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("User Input");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                showAlert("Invalid input! Please enter a valid decimal number.");
                return readDoubleFromUser(prompt);
            }
        } else {
            // Handle dialog canceled
            System.exit(0);
            return 0; // Not reachable
        }
    }

    public int readIntFromUser(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("User Input");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String input = result.get();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showAlert("Invalid input! Please enter a valid integer number.");
                return readIntFromUser(prompt);
            }
        } else {
            // Handle dialog canceled
            System.exit(0);
            return 0; // Not reachable
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void initializeGrid() {
        grid = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int value = readIntFromUser("Enter value for cell [" + i + "][" + j + "]:");
                grid[i][j] = value;
            }
        }
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
        qTable = new double[gridSize * gridSize][actions.length];
        int it = 0;
        int currentState;
        int nextState;

        while (it < maxEpoch) {
            resetState();
            while (!finished()) {
                currentState = stateI * gridSize + stateJ;
                int act = chooseAction(epsilon);
                nextState = executeAction(act);
                int nextBestAct = chooseAction(0);

                // Bellman equation
                qTable[currentState][act] = qTable[currentState][act] + alpha * (grid[stateI][stateJ] + gamma * qTable[nextState][nextBestAct] - qTable[currentState][act]);
            }
            it++;
        }
    }

    public void showQTable(){
        System.out.println("---------------- Q Table ----------------");
        for (double[] line : qTable) {
            System.out.println(Arrays.toString(line));
        }
    }

    public void showRoadToTarget(){
        resetState();
        System.out.println("\n-------- Agent Road To Target ---------");

        int i = 0;
        while (!finished()) {
            int act = chooseAction(0);
            System.out.println("state : " + (stateI * gridSize + stateJ) + " -> action : " + directions(act) + "(" + act + ")");

            buttons[stateI][stateJ].setStyle("-fx-background-color: blue;");

            executeAction(act);
            i++;
        }

        System.out.println("final state : " + (stateI * gridSize + stateJ));
    }

    public void showParameters(){
        System.out.println("\n-------- Parameters -----------");
        System.out.println(" α = " + alpha);
        System.out.println(" γ = " + gamma);
        System.out.println(" ε = " + epsilon);

        System.out.println(" Grid Size = " + gridSize);
        System.out.println(" Epochs Numbre = " + maxEpoch);
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

    public int chooseAction(double epsilon) {
        Random random = new Random();
        double bestQ = 0;
        int act = 0;
        if (random.nextDouble() < epsilon) {
            // Exploration
            act = random.nextInt(actions.length);
        } else {
            // Exploitation
            int st = stateI * gridSize + stateJ;
            for (int i = 0; i < actions.length; i++) {
                if (qTable[st][i] > bestQ) {
                    bestQ = qTable[st][i];
                    act = i;
                }
            }
        }
        return act;
    }

    public int executeAction(int act) {
        stateI = Math.max(0, Math.min(actions[act][0] + stateI, gridSize - 1));
        stateJ = Math.max(0, Math.min(actions[act][1] + stateJ, gridSize - 1));
        buttons[stateI][stateJ].setText(String.valueOf(stateI * gridSize + stateJ));

        // Update GUI with agent's position and color
        if (grid[stateI][stateJ] == 1) {
            buttons[stateI][stateJ].setStyle("-fx-background-color: green;");
        } else if (grid[stateI][stateJ] == -1) {
            buttons[stateI][stateJ].setStyle("-fx-background-color: red;");
        }

        return stateI * gridSize + stateJ;
    }
}