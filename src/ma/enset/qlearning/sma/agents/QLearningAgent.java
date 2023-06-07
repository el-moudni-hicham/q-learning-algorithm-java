package ma.enset.qlearning.sma.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Random;

import static ma.enset.qlearning.sma.helpers.QLUtils.*;

public class QLearningAgent extends Agent{
    private int[][] grid;
    private double[][] qTable = new double[GRID_SIZE*GRID_SIZE][ACTIONS_SIZE];
    private int[][] actions;
    private int stateI;
    private int stateJ;

    int steps = 0;

    public void setup() {
        actions = new int[][]{
                {0,-1}, //Left
                {0,1}, //Right
                {1,0}, //Bottom
                {-1,0} //Top
        };

        grid = new int[][]{
                {0, 0, 0, 0, 0, 0},
                {0, 0, -1, -1, 0, 0},
                {0, 0, 0 , 0, 0, 0},
                {-1, 0, 0, 0, 0, -1},
                {0, 0, -1, -1, 0, 0},
                {0, 0, 1, 0, 0, 0}
        };

        addBehaviour(new Behaviour() {
            @Override
            public void action() {
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

                        steps++;
                    }
                    it++;
                }
                showQTable();
                showRoadToTarget();
                showSteps();
            }

            public boolean done() {
                sendAgentInfo();
                return true;
            }
        });


    }

    public void showQTable(){
        System.out.println("---------------- Q Table ----------------");
        for (double[] line:qTable) {
            System.out.println(Arrays.toString(line));
        }
    }

    public void showRoadToTarget(){
        resetState();
        System.out.println("-------- Agent Road To Target ---------");

        int i = 0;


        while (!finished()){
            int act = chooseAction(0);
            System.out.println("state : " + (stateI * GRID_SIZE + stateJ) + " -> action : " + derections(act) + "("+act+")");
            executeAction(act);
            i++;
        }

        System.out.println("final state : " + (stateI * GRID_SIZE + stateJ));
    }

    public void showSteps(){
        System.out.println("Total steps of agent in all epochs : " + steps + " step");
    }
    public boolean finished(){
        return grid[stateI][stateJ] == 1;
    }

    public String derections(int i){
        switch (i){
            case 0 : return "LEFT";
            case 1 : return "RIGHT";
            case 2 : return "BOTTOM";
            case 3 : return "TOP";
            default: return " ";
        }
    }
    public void resetState(){
        stateI = 0;
        stateJ = 0;
    }

    public int chooseAction(double eps){
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

    public int executeAction(int act){
        stateI = Math.max(0, Math.min(actions[act][0]+stateI, GRID_SIZE-1));
        stateJ = Math.max(0, Math.min(actions[act][1]+stateJ, GRID_SIZE-1));
        return stateI*GRID_SIZE + stateJ;
    }

    public int getSteps() {
        return steps;
    }

    public void sendAgentInfo(){
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("ql");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] masterDescription = DFService.search(this, dfAgentDescription);
            ACLMessage aclMessage = new ACLMessage();
            aclMessage.addReceiver(masterDescription[0].getName());
            aclMessage.setContent(String.valueOf(getSteps()));
            send(aclMessage);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
