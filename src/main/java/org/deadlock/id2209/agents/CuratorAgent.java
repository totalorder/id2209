package org.deadlock.id2209.agents;


import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import org.deadlock.id2209.messages.ArtifactMessage;
import org.deadlock.id2209.messages.ArtifactsMessage;
import org.deadlock.id2209.messages.RequestArtifactMessage;
import org.deadlock.id2209.model.Artifact;
import org.deadlock.id2209.util.Communicator;
import org.deadlock.id2209.util.DFRegistry;
import org.deadlock.id2209.util.ReceiveBehavior;

import java.util.HashSet;
import java.util.Optional;
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
        "arts"
    ));

    artifacts.add(new Artifact(
        2,
        "The Persistence of Memory",
        "Salvador Dalí",
        1931,
        "Spain",
        "arts"
    ));

    artifacts.add(new Artifact(
        3,
        "David",
        "Michelangelo",
        1504,
        "Italy",
        "sculpture"
    ));

    System.out.println(getLocalName() + " registering curation: " + getAID());
    dfRegistry.registerService("curation");

    addBehaviour(receiveBehaviour);
  }

  private final Behaviour receiveBehaviour = new ReceiveBehavior(this) {
    @Override
    public void onObjectReceived(ACLMessage message, Object contentObject) {
      if (contentObject instanceof RequestArtifactMessage) {
        final RequestArtifactMessage requestArtifactMessage = (RequestArtifactMessage)contentObject;
        final Optional<Artifact> artifact = artifacts.stream()
            .filter(item -> item.id == requestArtifactMessage.artifactId)
            .findFirst();
        if (artifact.isPresent()) {
          communicator.send(message.getSender(), ArtifactMessage.create(artifact.get()));
        }
      }
    }

    @Override
    public void onMessageReceived(final ACLMessage message, final String content) {
      if (content.equals("request_artifacts")) {
        communicator.send(message.getSender(), ArtifactsMessage.create(artifacts));
      }
    }
  };
}