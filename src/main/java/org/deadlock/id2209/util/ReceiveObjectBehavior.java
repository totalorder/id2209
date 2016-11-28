package org.deadlock.id2209.util;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public abstract class ReceiveObjectBehavior<T> extends CyclicBehaviour {
  private final MessageTemplate messageTemplate;
  private final Class<T> type;

  public ReceiveObjectBehavior(final Agent agent, final Class<T> type) {
    super(agent);
    this.type = type;
    messageTemplate = new MessageTemplate(msg -> {
      try {
        final Object contentObject = msg.getContentObject();
        try {
          type.cast(contentObject);
          return true;
        } catch (ClassCastException e) {
          return false;
        }
      } catch (UnreadableException e) {
        return false;
      }
    });
  }

  public void action() {
    final ACLMessage message = getAgent().receive(messageTemplate);
    if (message != null) {
      try {
        final Object contentObject = message.getContentObject();
        try {
          final T content = type.cast(contentObject);
          System.out.println(myAgent.getLocalName() + ": Received " + content + " from " + message.getSender().getLocalName());
          onObjectReceived(message, content);
        } catch (ClassCastException e) {
          throw new RuntimeException(e);
        }
      } catch (UnreadableException e) {
        throw new RuntimeException(e);
      }
    }
    block();
  }

  public abstract void onObjectReceived(final ACLMessage message, final T contentObject);
}
