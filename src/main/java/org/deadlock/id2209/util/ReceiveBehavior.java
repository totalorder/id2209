package org.deadlock.id2209.util;

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
          onMessageReceived(message, (String)contentObject);
        } else {
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
