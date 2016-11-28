package org.deadlock.id2209.u3.agents;


import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.deadlock.id2209.u3.AMSUtil;
import org.deadlock.id2209.u3.dutchauction.DutchAuctionInitiator;
import org.deadlock.id2209.util.DFRegistry;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CuratorAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  public List<AID> artifactPurchasers;
  private final Random random = new Random();
  private int nextArtifactId = random.nextInt(1000);
  private int stopAt = nextArtifactId + 1;

  protected void setup() {
    getContentManager().registerLanguage(new SLCodec());
    getContentManager().registerOntology(MobilityOntology.getInstance());

    Object[] args = getArguments();
    if (args.length == 2) {
      final int clones = (int)args[0];
      final String[] destinationNames = (String[])args[1];
      for (String destinationName: destinationNames) {
        AMSUtil.clone(this, clones, destinationName);
      }
    }

    addBehaviour(receiveCommands);
  }

  private Behaviour receiveCommands = AMSUtil.createMobilityReceiveBehavior(this);

  private void init() {
    getContentManager().registerLanguage(new SLCodec());
    getContentManager().registerOntology(MobilityOntology.getInstance());

    addBehaviour(findPurchasers);
  }

  protected void afterClone() {
    System.out.println(getLocalName() + ": Cloned myself to location: " + here().getName());
    init();
  }

  protected void afterMove() {
    System.out.println(getLocalName() + ": Moved myself to location: " + here().getName());
    System.out.println(getLocalName() + " registering curation: " + getAID());
    dfRegistry.registerService("curation");
    addBehaviour(receivePricesBehavior);
    addBehaviour(announceBestPrice);
  }

  private DutchAuctionInitiator createDutchAuction() {
    final int startPrice = (int)(10000 * (random.nextFloat() * 0.5 + 0.75));
    final int stopPrice = (int)(startPrice * 0.5 * (random.nextFloat() / 2 + 0.75));
    return new DutchAuctionInitiator(
        this, artifactPurchasers, nextArtifactId++, startPrice, stopPrice);
  }

  private DutchAuctionInitiator dutchAuction;
  private int bestPrice = 0;
  private Behaviour findPurchasers = new SimpleBehaviour() {

    @Override
    public void action() {
      artifactPurchasers = dfRegistry.findServiceProviders("artifact-purchasing" + here().getName());
    }

    @Override
    public boolean done() {
      return artifactPurchasers.size() > 0;
    }

    @Override
    public int onEnd() {
      if (dutchAuction != null && dutchAuction.getBestPrice() > bestPrice) {
        bestPrice = dutchAuction.getBestPrice();
      }
      if (nextArtifactId <= stopAt) {
        final SequentialBehaviour createFindAndAuctionBehavior = new SequentialBehaviour();
        dutchAuction = createDutchAuction();
        createFindAndAuctionBehavior.addSubBehaviour(dutchAuction);
        createFindAndAuctionBehavior.addSubBehaviour(findPurchasers);

        addBehaviour(createFindAndAuctionBehavior);
      } else {
        System.out.println(String.format("%s: I'm all out of art!", myAgent.getLocalName()));
        System.out.println(String.format("%s: My best price: %d", myAgent.getLocalName(), bestPrice));
        AMSUtil.move(myAgent, "auctioneers");
      }
      return 0;
    }
  };

  private Behaviour announceBestPrice = new TickerBehaviour(this, 1000) {
    @Override
    protected void onTick() {
      final List<AID> curators = dfRegistry.findServiceProviders("curation");
      for (AID curator : curators) {
        if (curator.equals(getAID())) {
          continue;
        }
        final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(curator);
        try {
          message.setContentObject(bestPrice);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        send(message);
      }

    }
  };

  private Behaviour receivePricesBehavior = new CyclicBehaviour() {
    @Override
    public void action() {
      final ACLMessage message = myAgent.receive();
      if (message != null) {
        try {
          int otherBestPrice = (int)message.getContentObject();
          if (otherBestPrice > bestPrice) {
            bestPrice = otherBestPrice;
          }
          System.out.println(String.format("%s: Best price: %d", myAgent.getLocalName(), bestPrice));
        } catch (UnreadableException e) {
          throw new RuntimeException(e);
        }
      } else {
        block();
      }
    }
  };
}