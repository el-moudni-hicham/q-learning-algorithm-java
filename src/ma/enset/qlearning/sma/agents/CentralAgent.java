package ma.enset.qlearning.sma.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.qlearning.sma.entites.QLEntity;
import ma.enset.qlearning.sma.helpers.QLUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CentralAgent extends Agent {
    List<QLEntity> entities = new ArrayList<>();
    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setName("central");
        serviceDescription.setType("ql");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this,dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        addBehaviour(new Behaviour() {
            int it=0;
            @Override
            public void action() {
                ACLMessage aclMessage=blockingReceive();
                String content=aclMessage.getContent();
                System.out.println("---------------------------------------------------");
                System.out.println("The Steps Number Of Agent : " + aclMessage.getSender().getLocalName());
                System.out.println("Is : " + content);

                entities.add(new QLEntity(aclMessage.getSender().getLocalName(), Integer.valueOf(content)));
                it++;
            }

            @Override
            public boolean done() {
                if(it == QLUtils.AGENTS_NUMBER) {
                    Collections.sort(entities);
                    String name = entities.get(0).getName();
                    int bestSteps = entities.get(0).getStepsNb();

                    System.out.println("\n<<<<<<<<<<<<<<<<<<<<<<<<<< Best Performance >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("Agent Name : " + name);
                    System.out.println("Best Steps Number : " + bestSteps);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}