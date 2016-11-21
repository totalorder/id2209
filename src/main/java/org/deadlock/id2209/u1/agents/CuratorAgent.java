package org.deadlock.id2209.u1.agents;


import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.SimpleAchieveREResponder;
import org.deadlock.id2209.u1.messages.ArtifactMessage;
import org.deadlock.id2209.u1.messages.ArtifactsMessage;
import org.deadlock.id2209.u1.messages.RequestArtifactMessage;
import org.deadlock.id2209.u1.model.Artifact;
import org.deadlock.id2209.util.Communicator;
import org.deadlock.id2209.util.DFRegistry;
import org.deadlock.id2209.util.ReceiveBehavior;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The curator agent is responsible for answering answering questions about the artifacts
 * It responds to two types of requests:
 * 1. RequestArtifactMessage: Returns the information for the artifact with the given id
 * 2. request_artifacts: Returns a list of all its artifacts
 */
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
    dfRegistry.registerService("curation", new Property("genre", "arts"), new Property("genre", "sculpture"));

    addBehaviour(receiveBehaviour);
  }

  private final Behaviour receiveBehaviour = new ReceiveBehavior(this) {
    @Override
    public void onObjectReceived(ACLMessage message, Object contentObject) {
      /**
       * Respond to RequestArtifactMessage with a ArtifactMessage for the given artifactId if it exists
       */
      addBehaviour(new SimpleAchieveREResponder(myAgent, new MessageTemplate(msg -> {
        try {
          return msg.getContentObject() instanceof RequestArtifactMessage;
        } catch (UnreadableException e) {
          return false;
        }
      })) {
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
          final RequestArtifactMessage requestArtifactMessage = (RequestArtifactMessage)contentObject;
          final Optional<Artifact> artifact = artifacts.stream()
              .filter(item -> item.id == requestArtifactMessage.artifactId)
              .findFirst();
          if (artifact.isPresent()) {
            final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            try {
              message.setContentObject(ArtifactMessage.create(artifact.get()));
              return message;
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
          return null;
        }
      });

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
      /**
       * Respond to the string "request_artifacts" with an ArtifactsMessage containing all artifacts
       */
      if (content.equals("request_artifacts")) {
        communicator.send(message.getSender(), ArtifactsMessage.create(artifacts));
      }
    }
  };
}