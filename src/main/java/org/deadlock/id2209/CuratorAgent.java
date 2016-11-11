package org.deadlock.id2209;


import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CuratorAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  private final Communicator communicator = new Communicator(this);
  private final Set<Artifact> artifacts = new HashSet<>();

  protected void setup() {
    artifacts.add(new Artifact(
        1,
        "Mona Lisa",
        "Leonardo da Vinci",
        1517,
        "France",
        "painting"
    ));

    System.out.println("Registering curation: " + getAID());
    dfRegistry.registerService("curation");

    addBehaviour(receiveBehaviour);
  }

  private final Behaviour receiveBehaviour = new ReceiveBehavior(this) {
    @Override
    public void onObjectReceived(ACLMessage message, Object contentObject) {
      throw new NotImplementedException();
    }

    @Override
    public void onMessageReceived(final ACLMessage message, final String content) {
      if (content.equals("request_artifacts")) {
        communicator.send(message.getSender(), ArtifactsMessage.create(artifacts));
      }
    }
  };
}