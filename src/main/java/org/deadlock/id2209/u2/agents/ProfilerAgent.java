package org.deadlock.id2209.u2.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import org.deadlock.id2209.u2.Holdings;
import org.deadlock.id2209.u2.dutchauction.BiddingStrategy;
import org.deadlock.id2209.u2.dutchauction.DutchAuctionResponder;
import org.deadlock.id2209.u2.dutchauction.HighBidderStrategy;
import org.deadlock.id2209.u2.dutchauction.LowBidderStrategy;
import org.deadlock.id2209.util.DFRegistry;

import java.util.HashMap;
import java.util.Map;


public class ProfilerAgent extends Agent {
  private Map<String, BiddingStrategy> strategyMap = new HashMap<>();

  private final DFRegistry dfRegistry = new DFRegistry(this);
  private BiddingStrategy strategy;
  private final Holdings holdings = new Holdings(50000);

  protected void setup() {
    strategyMap.put("lowbidder", new LowBidderStrategy());
    strategyMap.put("highbidder", new HighBidderStrategy());

    Object[] args = getArguments();

    String strategyName = "lowbidder";
    if (args.length == 1) {
      strategyName = (String)args[0];
    }
    if (strategyMap.get(strategyName) == null) {
      throw new RuntimeException("Invalid strategy: " + strategyName);
    }

    this.strategy = strategyMap.get(strategyName);
    dutchAuctionResponder = new DutchAuctionResponder(this, strategy, holdings);


    System.out.println(getLocalName() + " registering artifact-purchasing: " + getAID());
    dfRegistry.registerService("artifact-purchasing");
    addBehaviour(dutchAuctionResponder);

    System.out.println(getLocalName() + " using strategy " + strategyName);
  }

  private Behaviour dutchAuctionResponder;
}