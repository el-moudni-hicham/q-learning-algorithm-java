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

public class SimpleContainer {
    public static void main(String[] args) throws StaleProxyException, InterruptedException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);


        for (int i = 0; i < QLUtils.AGENTS_NUMBER; i++) {
            AgentController mainAgent = agentContainer.createNewAgent("Q" + i, QLearningAgent.class.getName(), new Object[]{});
            mainAgent.start();
            sleep(3000);
        }

    }
}

