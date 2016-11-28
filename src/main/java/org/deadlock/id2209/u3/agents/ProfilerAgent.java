package org.deadlock.id2209.u3.agents;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.mobility.CloneAction;
import jade.domain.mobility.MobileAgentDescription;
import jade.domain.mobility.MobilityOntology;
import jade.domain.mobility.MoveAction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.deadlock.id2209.u3.Holdings;
import org.deadlock.id2209.u3.dutchauction.BiddingStrategy;
import org.deadlock.id2209.u3.dutchauction.DutchAuctionResponder;
import org.deadlock.id2209.u3.dutchauction.HighBidderStrategy;
import org.deadlock.id2209.u3.dutchauction.LowBidderStrategy;
import org.deadlock.id2209.u3.AMSUtil;
import org.deadlock.id2209.util.DFRegistry;

import java.util.HashMap;
import java.util.Map;


public class ProfilerAgent extends Agent {
  private Map<String, BiddingStrategy> strategyMap = new HashMap<>();

  private final DFRegistry dfRegistry = new DFRegistry(this);
  private BiddingStrategy strategy;
  private final Holdings holdings = new Holdings(50000);

  protected void setup() {
    getContentManager().registerLanguage(new SLCodec());
    getContentManager().registerOntology(MobilityOntology.getInstance());

    strategyMap.put("lowbidder", new LowBidderStrategy());
    strategyMap.put("highbidder", new HighBidderStrategy());

    Object[] args = getArguments();

    String strategyName = "lowbidder";
    if (args.length >= 1) {
      strategyName = (String)args[0];
    }

    if (args.length == 3) {
      final int clones = (int)args[1];
      final String destinationName = (String)args[2];
      AMSUtil.clone(this, clones, destinationName);
    }

    if (strategyMap.get(strategyName) == null) {
      throw new RuntimeException("Invalid strategy: " + strategyName);
    }
    this.strategy = strategyMap.get(strategyName);

    addBehaviour(receiveCommands);
  }

  private void init() {
    dutchAuctionResponder = new DutchAuctionResponder(this, strategy, holdings);

    System.out.println(getLocalName() + " registering artifact-purchasing-" + here().getName() + ": " + getAID());
    dfRegistry.registerService("artifact-purchasing" + here().getName());
    addBehaviour(dutchAuctionResponder);

    System.out.println(getLocalName() + " using strategy " + strategy.getClass().getSimpleName());
  }

  protected void beforeClone() {
//    System.out.println("Cloning myself to location : " + here().getName());
  }

  protected void afterClone() {
    System.out.println(getLocalName() + ": Cloned myself to location: " + here().getName());
    init();
  }

  protected void beforeMove() {
    System.out.println("Moving now to location : " + here().getName());
  }

  protected void afterMove() {
    System.out.println("Arrived at location : " + here().getName());
  }

  private Behaviour receiveCommands = AMSUtil.createMobilityReceiveBehavior(this);


  private Behaviour dutchAuctionResponder;
}