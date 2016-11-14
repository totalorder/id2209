package org.deadlock.id2209.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 * Subscribe to a number of services and log any new providers for these services
 */
public class DFSubscriberAgent extends Agent {
  protected void setup() {
    final String[] services = new String[]{"guiding", "curation"};
    for (final String service : services) {
      subscribeToServices.addSubBehaviour(new OneShotBehaviour() {
        @Override
        public void action() {
          final DFAgentDescription dfAgentDescription = new DFAgentDescription();
          final ServiceDescription serviceDescription = new ServiceDescription();
          final SearchConstraints searchConstraints = new SearchConstraints();
          serviceDescription.setType(service);
          dfAgentDescription.addServices(serviceDescription);
          final ACLMessage subscriptionMessage = DFService.createSubscriptionMessage(
              myAgent, myAgent.getDefaultDF(), dfAgentDescription, searchConstraints);
          myAgent.send(subscriptionMessage);
        }
      });
    }

    subscribeThenReceive.addSubBehaviour(subscribeToServices);
    subscribeToServices.addSubBehaviour(receiveBehaviour);

    addBehaviour(subscribeThenReceive);
  }

  private final SequentialBehaviour subscribeThenReceive = new SequentialBehaviour(this);

  private final ParallelBehaviour subscribeToServices = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL) {
  };

  private final Behaviour receiveBehaviour = new CyclicBehaviour(this) {
    @Override
    public void action() {
      final ACLMessage message = getAgent().receive();
      if (message != null) {
        try {
          final DFAgentDescription[] dfds =
              DFService.decodeNotification(message.getContent());
          for (DFAgentDescription dfd : dfds) {
            final ServiceDescription sd = (ServiceDescription) dfd.getAllServices().next();
            System.out.println("dfsubscriber found agent providing service " + sd.getType() + ": " + sd.getName());
          }
        } catch (FIPAException e) {
          e.printStackTrace();
        }
      }
      block();
    }
  };
}