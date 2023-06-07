# Q-Learning Algorithm Report

This repository contains a comprehensive report on the Q-Learning algorithm, a popular reinforcement learning technique. The report covers the algorithm's fundamentals, implementation, and applications.

## Table of Contents

1. [Introduction](#introduction)
2. [Q-Learning Algorithm](#q-learning-algorithm)
    - [Q-Table](#q-table)
    - [Bellman equation](#bellman-equation)
3. [Exploration vs. Exploitation](#exploration-vs-exploitation)
4. [Java Implementation](#java-implementation)
    - [Sequential](#sequential)
    - [Multi Agent System](#multi-agent-system)
    - [JavaFX](#javafx)
6. [Applications](#applications)
7. [Conclusion](#conclusion)


## Introduction

Q-Learning is a model-free reinforcement learning algorithm that aims to find the best action-selection policy for a given environment. It is an off-policy algorithm that learns the optimal policy by estimating the action-value function, which represents the expected future rewards for taking a specific action in a given state.

## Q-Learning Algorithm

### Q-Table

The Q-Learning algorithm uses a Q-table to store the estimated values of each state-action pair. The rows in the table represent the different states, while the columns represent the possible actions. The values in the table are updated iteratively as the agent explores the environment and learns from its experiences.

### Bellman equation

The Q-Learning algorithm updates the Q-table using the following formula:

`Q(s, a) = Q(s, a) + α * (R(s, a) + γ * max(Q(s', a')) - Q(s, a))`

where:
- `Q(s, a)` is the current Q-value for state `s` and action `a`
- `α` is the learning rate (0 < α ≤ 1)
- `R(s, a)` is the immediate reward for taking action `a` in state `s`
- `γ` is the discount factor (0 ≤ γ < 1)
- `max(Q(s', a'))` is the maximum Q-value for the next state `s'` and all possible actions `a'`

## Exploration vs. Exploitation

A key challenge in reinforcement learning is balancing exploration and exploitation. Exploration involves trying new actions to discover their effects, while exploitation involves choosing the action with the highest estimated Q-value. A common approach to balance exploration and exploitation is using an ε-greedy strategy, where the agent chooses a random action with probability ε and the action with the highest Q-value with probability 1 - ε.

## Java Implementation

### Project Structure 

```
D:.
└───src
    └───ma
        └───enset
            └───qlearning
                ├───gui
                │       QLearningAgentGUI.java
                │
                ├───sequential
                │       QLearning.java
                │       Test.java
                │
                └───sma
                    ├───agents
                    │       QLearningAgent.java
                    │
                    └───containers
                            MainContainer.java
                            SimpleContainer.java
```

Here's a simple Java implementation of the Q-Learning algorithm:

```java
package ma.enset.qlearning.sequential;

import java.util.Arrays;
import java.util.Random;

public class QLearning {

    // Q Learning Utils
    private final double ALPHA = 0.1;
    private final double GAMMA = 0.9;
    private final double EPS = 0.4;
    private final int MAX_EPOCH = 100000;
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
                {0,0,0,0,0,-1},
                {0,-1,0,0,0,0},
                {-1,-1,0,0,0,0},
                {0,0,0,0,0,1},
                {0,0,0,0,0,0},
                {0,0,0,0,0,-1},
        };
    }
    
    // The rest of methods ...
} 

```

### Methods Explecation :

`chooseAction(double eps)` : This method selects the next action for the agent to take. It takes a double
argument that represents the exploration rate, and returns an integer that represents the index of the
action in the actions array. It first generates a random number using the Random class, and checks if
the random number is less than the exploration rate. If it is, it selects a random action. Otherwise,
it selects the action with the highest expected reward in the current state.

```java
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
```

`executeAction(int act)` : This method updates the agent's position based on the action it takes.
It takes an integer argument that represents the index of the action in the actions array,
and returns an integer that represents the index of the new state in the Q-table. It updates
the values of the stateI and stateJ variables based on the action, and then calculates the index
of the new state in the Q-table using the formula `stateI*GRID_SIZE + stateJ` .


```java
    private int executeAction(int act){
        stateI = Math.max(0, Math.min(actions[act][0]+stateI, GRID_SIZE-1));
        stateJ = Math.max(0, Math.min(actions[act][1]+stateJ, GRID_SIZE-1));
        return stateI*GRID_SIZE + stateJ;
    }
```

`run()` : This is the main method of the QLearning class. It runs the Q-learning algorithm until the maximum 
number of iterations is reached. It calls the resetState() method to set the agent's position to the starting
position, and then enters a loop that continues until the finished() method returns true. In each iteration of 
the loop, it selects an action using the chooseAction() method, executes the action using the executeAction()
method, and updates the Q-table using the Bellman equation. Finally, it calls the showResult() method to print
the Q-table and the path the agent took to reach the goal state.

```java
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
                qTable[currentState][act] =   qTable[currentState][act] + ALPHA * (
                                                grid[stateI][stateJ]
                                              + GAMMA * qTable[nextState][nextBestAct] 
                                              - qTable[currentState][act]
                                            );

                it++;
            }
        }
        showResult();
    }
```

`showResult()` :
```java
    private void showResult(){
        System.out.println("---------------- Q Table ----------------");
        for (double[] line:qTable) {
            System.out.println(Arrays.toString(line));
        }

        resetState();
        System.out.println("-------- Agent Road To Target ---------");

        while (!finished()){
            int act = chooseAction(0);
            System.out.println("state : " + (stateI * GRID_SIZE + stateJ) + " -> action : " 
                                + derections(act) + "("+act+")");
            executeAction(act);
        }

        System.out.println("final state : " + (stateI * GRID_SIZE + stateJ));
    }
```
`finished()` : This method checks if the agent has reached the goal state. It returns true if the value of 
the current state in the grid array is 1, indicating that the agent has reached the goal state.

```java
    private boolean finished(){
        return grid[stateI][stateJ] == 1;
    }
```



`derections(int i)` : This method returns a string representation of the action taken by the agent. 
It takes an integer argument that represents the index of the action in the actions array, and returns
a string that corresponds to the direction of the action.

```java
   private String derections(int i){
        switch (i){
           case 0 : return "LEFT";
           case 1 : return "RIGHT";
           case 2 : return "BOTTOM";
           case 3 : return "TOP";
           default: return " ";
        }
   }
```


`resetState()` : This method sets the agent's position to the starting position. It sets the values of the 
stateI and stateJ variables to 0.
```java
   private void resetState(){
        stateI = 0;
        stateJ = 0;
   }
```

## Applications

Q-Learning has been successfully applied to various domains, including:

1. Robotics: Autonomous navigation and control of robots.
2. Game playing: Learning optimal strategies for playing games like chess, Go, and poker.
3. Resource allocation: Optimizing the allocation of resources in computer networks and manufacturing systems.
4. Finance: Portfolio management and algorithmic trading.

### Sequential
[link to code](https://github.com/el-moudni-hicham/q-learning-algorithm-java/tree/master/src/ma/enset/qlearning/sequential)
> Result 

  - Q Table :
  
![image](https://github.com/el-moudni-hicham/q-learning-algorithm-java/assets/85403056/29b9da3b-d7dd-4c37-ad4f-eb5c88239279)

  - Trajectory :
  
![image](https://github.com/el-moudni-hicham/q-learning-algorithm-java/assets/85403056/482189e8-9dee-4ac5-80e3-7ed386e8e522)


### Multi Agent System

[link to code](https://github.com/el-moudni-hicham/q-learning-algorithm-java/tree/master/src/ma/enset/qlearning/sma)

This class to create 5 agents to find the target :

```java
public class SimpleContainer {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);

        for (int i = 0; i < 5; i++) {
            AgentController mainAgent = agentContainer.createNewAgent(String.valueOf(i),
            QLearningAgent.class.getName(), new Object[]{});
            mainAgent.start();
            sleep(3000);
        }


    }
}
```

![image](https://github.com/el-moudni-hicham/q-learning-algorithm-java/assets/85403056/96325512-87f8-45f1-bfcb-534df3360300)

### JavaFX
[link to code](https://github.com/el-moudni-hicham/q-learning-algorithm-java/tree/master/src/ma/enset/qlearning/gui)
  - GUI :
The blue color is the tarjectory to the target 

![image](https://github.com/el-moudni-hicham/q-learning-algorithm-java/assets/85403056/941a04b3-f8b0-4fbe-9e56-29577345d12e)

![image](https://github.com/el-moudni-hicham/q-learning-algorithm-java/assets/85403056/650d7964-4b41-4fd7-a6a3-1e790036cc69)

## Recent Advancements

In recent years, researchers have made several advancements to enhance the performance and applicability of Q-Learning. Some notable advancements include:

- Deep Q-Networks (DQN): Combining Q-Learning with deep neural networks to handle high-dimensional state spaces and improve learning efficiency.
- Double Q-Learning: Addressing overestimation bias in Q-Learning by decoupling action selection and evaluation.
- Prioritized Experience Replay: Prioritizing important transitions in the agent's memory for more efficient learning.
- Distributed Q-Learning: Parallelizing Q-Learning across multiple agents or computing resources for faster convergence and scalability.
These advancements have pushed the boundaries of Q-Learning and enabled its successful application to even more complex problems.

## Conclusion

The Q-Learning algorithm is a powerful reinforcement learning technique that has been widely used in various applications. Its ability to learn the optimal policy without requiring a model of the environment makes it suitable for solving complex problems with unknown dynamics.


