package ma.enset.qlearning.sma.containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ma.enset.qlearning.sma.agents.CentralAgent;
import ma.enset.qlearning.sma.agents.QLearningAgent;
import ma.enset.qlearning.sma.helpers.QLUtils;

import static java.lang.Thread.sleep;

public class CentralContainer {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);


        AgentController centralAgent = agentContainer.createNewAgent("CentralAgent", CentralAgent.class.getName(), new Object[]{});
        centralAgent.start();
    }
}