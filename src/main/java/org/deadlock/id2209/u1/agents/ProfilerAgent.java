package org.deadlock.id2209.u1.agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.deadlock.id2209.u1.messages.ArtifactMessage;
import org.deadlock.id2209.u1.messages.RequestArtifactMessage;
import org.deadlock.id2209.u1.messages.RequestTourMessage;
import org.deadlock.id2209.u1.messages.TourMessage;
import org.deadlock.id2209.u1.model.Artifact;
import org.deadlock.id2209.u1.model.Profile;
import org.deadlock.id2209.u1.model.Tour;
import org.deadlock.id2209.util.Communicator;
import org.deadlock.id2209.util.DFRegistry;
import org.deadlock.id2209.util.ReceiveBehavior;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * The profiler agent is responsible for aquiring tours and getting information
 * about the artifacts in the tour
 * It handles two types of messages:
 * TourMessage: Receive a new Tour received from a guide
 * ArtifactMessage: Print the info about the artifact and move on to the next artifact in the tour
 */
public class ProfilerAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  private final Communicator communicator = new Communicator(this);
  private AID guide;
  private AID curator;
  private Tour tour;

  private final Profile profile = new Profile(28, "Software Engineer", "male", "arts");

  protected void setup() {
    addBehaviour(findCuratorBehavior);
    addBehaviour(receiveBehaviour);
    addBehaviour(createFindTourBehavior());
    addBehaviour(requestArtifactInfo);
  }

  /**
   * Find a guide and ask for a tour
   */
  private Behaviour createFindTourBehavior() {
    return new WakerBehaviour(this, 1000) {
      protected void onWake() {
        if (guide == null) {
          guide = dfRegistry.findService("guiding");
          System.out.println(getLocalName() + " found guide: " + guide);
        }

        if (guide != null && tour == null) {
          communicator.send(guide, RequestTourMessage.create(profile));
        }

        if (tour == null) {
          addBehaviour(createFindTourBehavior());
        }
      }
    };
  }

  /**
   * Ask for information about the next artifact in the tour
   */
  private final Behaviour requestArtifactInfo = new TickerBehaviour(this, 1000) {
    @Override
    protected void onTick() {
      if (curator != null && tour != null && !tour.artifactIds.isEmpty()) {
        communicator.send(curator, RequestArtifactMessage.create(tour.artifactIds.get(0)));
      }
    }
  };

  /**
   * Find a curator
   */
  private final Behaviour findCuratorBehavior = new TickerBehaviour(this, 1000) {
    @Override
    protected void onTick() {
      if (curator == null) {
        curator = dfRegistry.findService("curation");
        System.out.println(getLocalName() + " found curator: " + curator);
      }
    }
  };

  private final Behaviour receiveBehaviour = new ReceiveBehavior(this) {
    @Override
    public void onObjectReceived(ACLMessage message, Object contentObject) {
      if (contentObject instanceof TourMessage) {
        // Update the tour with the received one if there isn't already an active tour
        final TourMessage tourMessage = (TourMessage)contentObject;
        if (tour == null) {
          tour = tourMessage.tour;
        }
      } else if (contentObject instanceof ArtifactMessage) {
        // Print information about the artifact it it's next in the tour
        // Remove the artifact from the tour and ask for a new tour if there are no more artifacts in the tour anymore
        final ArtifactMessage artifactMessage = (ArtifactMessage)contentObject;
        final Artifact artifact = artifactMessage.artifact;
        if (!tour.artifactIds.isEmpty() && tour.artifactIds.get(0) == artifact.id) {
          tour.artifactIds.remove(0);
          System.out.println(getLocalName() + " " + artifactMessage.artifact.name + ". Hmm.. Interesting!");

          if (tour.artifactIds.isEmpty()) {
            tour = null;
            System.out.println(getLocalName() + " tour is ended!");
            addBehaviour(createFindTourBehavior());
          }
        } else {
          System.out.println(getLocalName() + " it's not time for artifact " + artifact.id);
        }
      } else {
        throw new NotImplementedException();
      }
    }

    @Override
    public void onMessageReceived(final ACLMessage message, final String content) {
      throw new NotImplementedException();
    }
  };
}