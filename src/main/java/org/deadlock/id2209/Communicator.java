package org.deadlock.id2209;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

public class Communicator {
  private final Agent agent;

  public Communicator(final Agent agent) {
    this.agent = agent;
  }

  public void send(final AID receiver, final Object contentObject) {
    final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
    message.addReceiver(receiver);
    try {
      message.setContentObject((Serializable)contentObject);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    agent.send(message);
  }
}
