package org.deadlock.id2209.u2.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import org.deadlock.id2209.u2.Holdings;
import org.deadlock.id2209.u2.dutchauction.BiddingStrategy;
import org.deadlock.id2209.u2.dutchauction.DutchAuctionResponder;
import org.deadlock.id2209.u2.dutchauction.HighBidderStrategy;
import org.deadlock.id2209.util.DFRegistry;


public class ProfilerAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);
  private final BiddingStrategy strategy = new HighBidderStrategy();
  private final Holdings holdings = new Holdings(50000);

  protected void setup() {
    System.out.println(getLocalName() + " registering artifact-purchasing: " + getAID());
    dfRegistry.registerService("artifact-purchasing");
    addBehaviour(dutchAuctionResponder);
  }

  private Behaviour dutchAuctionResponder = new DutchAuctionResponder(this, strategy, holdings);
}