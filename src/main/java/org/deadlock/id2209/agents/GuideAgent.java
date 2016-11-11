package org.deadlock.id2209.agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.deadlock.id2209.messages.ArtifactsMessage;
import org.deadlock.id2209.model.Artifact;
import org.deadlock.id2209.util.Communicator;
import org.deadlock.id2209.util.DFRegistry;
import org.deadlock.id2209.util.ReceiveBehavior;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public class GuideAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  private final Communicator communicator = new Communicator(this);
  private AID curator;
  public Set<Artifact> artifacts;

  protected void setup() {
    addBehaviour(receiveBehaviour);
    addBehaviour(askForArtifacts);
  }

  private final Behaviour askForArtifacts = new TickerBehaviour(this, 1000) {
    @Override
    protected void onTick() {
      if (curator == null) {
        curator = dfRegistry.findService("curation");
        System.out.println("Curator: " + curator);
      }

      if (curator != null) {
        communicator.send(curator, "request_artifacts");
      }
    }
  };

  private final Behaviour receiveBehaviour = new ReceiveBehavior(this) {

    @Override
    public void onMessageReceived(ACLMessage message, String content) {
      throw new NotImplementedException();
    }

    @Override
    public void onObjectReceived(final ACLMessage message, final Object object) {
      if (object instanceof ArtifactsMessage) {
        final ArtifactsMessage artifactsMessage = (ArtifactsMessage) object;
        artifacts = artifactsMessage.artifacts;
      } else {
        throw new NotImplementedException();
      }
    }
  };
}