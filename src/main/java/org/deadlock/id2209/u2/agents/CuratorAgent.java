package org.deadlock.id2209.u2.agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import org.deadlock.id2209.u2.dutchauction.DutchAuctionInitiator;
import org.deadlock.id2209.util.DFRegistry;

import java.util.List;
import java.util.Random;

public class CuratorAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  public List<AID> artifactPurchasers;
  private final Random random = new Random();
  private int nextArtifactId = random.nextInt(1000);

  protected void setup() {
    addBehaviour(findPurchasers);
  }

//  private Behaviour createFindAndAuctionBehavior() {
//    final SequentialBehaviour findAndAuction = new SequentialBehaviour();
//    findAndAuction.addSubBehaviour(findPurchasers);
////    final int startPrice = (int)(10000 * (random.nextFloat() * 0.5 + 0.75));
////    final int stopPrice = (int)(startPrice * 0.5 * (random.nextFloat() / 2 + 0.75));
////    findAndAuction.addSubBehaviour(new DutchAuctionInitiator(
////        this, artifactPurchasers, nextArtifactId++, startPrice, stopPrice));
//    findAndAuction.addSubBehaviour(new OneShotBehaviour() {
//      @Override
//      public void action() {
//        myAgent.addBehaviour(createDutchAuction());
//      }
//    });
//
//    findAndAuction.addSubBehaviour(new OneShotBehaviour() {
//      @Override
//      public void action() {
//        myAgent.addBehaviour(createFindAndAuctionBehavior());
//      }
//    });
//    return findAndAuction;
//  }

  private Behaviour createDutchAuction() {
    final int startPrice = (int)(10000 * (random.nextFloat() * 0.5 + 0.75));
    final int stopPrice = (int)(startPrice * 0.5 * (random.nextFloat() / 2 + 0.75));
    return new DutchAuctionInitiator(
        this, artifactPurchasers, nextArtifactId++, startPrice, stopPrice);
  }

  private Behaviour findPurchasers = new SimpleBehaviour() {

    @Override
    public void action() {
      artifactPurchasers = dfRegistry.findServiceProviders("artifact-purchasing");
    }

    @Override
    public boolean done() {
      return artifactPurchasers.size() > 0;
    }

    @Override
    public int onEnd() {
      final SequentialBehaviour createFindAndAuctionBehavior = new SequentialBehaviour();
      createFindAndAuctionBehavior.addSubBehaviour(createDutchAuction());
      createFindAndAuctionBehavior.addSubBehaviour(findPurchasers);
      addBehaviour(createFindAndAuctionBehavior);
      return 0;
    }
  };
}