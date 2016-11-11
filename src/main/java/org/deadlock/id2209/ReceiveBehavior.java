package org.deadlock.id2209;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public abstract class ReceiveBehavior extends CyclicBehaviour {
  public ReceiveBehavior(final Agent agent) {
    super(agent);
  }

  public void action() {
    final ACLMessage message = getAgent().receive();
    if (message != null) {
      try {
        final Object contentObject = message.getContentObject();
        if (contentObject instanceof String) {
          final String contentString = (String)contentObject;
          System.out.println(" - " +
              myAgent.getLocalName() + " <- " +
              contentString);

          onMessageReceived(message, contentString);
        } else {
          System.out.println(" - " +
              myAgent.getLocalName() + " <- " +
              contentObject.toString());
          onObjectReceived(message, contentObject);
        }
      } catch (UnreadableException e) {
        throw new RuntimeException(e);
      }
    }
    block();
  }

  public abstract void onMessageReceived(final ACLMessage message, final String content);
  public abstract void onObjectReceived(final ACLMessage message, final Object contentObject);
}
