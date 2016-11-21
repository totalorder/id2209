package org.deadlock.id2209.u2.dutchauction;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import org.deadlock.id2209.util.ReceiveAllReplys;
import org.deadlock.id2209.util.ReceiveAllReplysAfterTimeout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DutchAuctionInitiator extends FSMBehaviour {
  private final List<AID> participants;
  private final int itemId;
  private final int stopPrice;
  private final String conversationId;
  private int currentPrice;
  private final int priceDecrement;
  private List<String> cfpReplyWithIds;
  private AID soldTo;

  public DutchAuctionInitiator(final Agent agent,
                               final List<AID> participants,
                               final int itemId,
                               final int startPrice,
                               final int stopPrice) {
    super(agent);
    this.participants = participants;
    this.itemId = itemId;
    this.currentPrice = startPrice;
    this.stopPrice = stopPrice;
    this.priceDecrement = (startPrice - stopPrice) / 10;
    this.cfpReplyWithIds = Stream
        .generate(() -> UUID.randomUUID().toString())
        .limit(participants.size())
        .collect(Collectors.toList());
    this.conversationId = UUID.randomUUID().toString();
    receiveProposals = new ReceiveAllReplys(
        myAgent, FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION, conversationId, cfpReplyWithIds);

    this.registerFirstState(start, "start");
    this.registerState(sendCfp, "send_cfps");
    this.registerState(receiveProposals, "receive_proposals");
    this.registerState(respondToProposals, "respond_to_proposals");
    this.registerDefaultTransition("start", "send_cfps");
    this.registerDefaultTransition("send_cfps", "receive_proposals");
    this.registerDefaultTransition("receive_proposals", "respond_to_proposals");
    this.registerLastState(end, "end");
    this.registerTransition("respond_to_proposals", "send_cfps", 0);
    this.registerTransition("respond_to_proposals", "end", 1);
  }

  @Override
  protected void handleStateEntered(Behaviour state) {
    state.reset();
  }

  private final Behaviour start = new OneShotBehaviour(myAgent) {
    @Override
    public void action() {
      participants.stream().forEach(participant -> {
        final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(participant);
        message.setContent(Integer.toString(itemId));
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
        message.setConversationId(conversationId);
        myAgent.send(message);

        System.out.println(String.format("%s: Starting auction for item %s", myAgent.getLocalName(), itemId));
      });
    }
  };

  private final Behaviour sendCfp = new OneShotBehaviour(myAgent) {
    @Override
    public void action() {
      System.out.println(String.format("%s: Sending cfp for item %s for price %s (stops at %s) to %s participants.",
          myAgent.getLocalName(), itemId, currentPrice, stopPrice, participants.size()));

      int index = 0;
      for(final AID participant : participants) {
          final ACLMessage message = new ACLMessage(ACLMessage.CFP);
          message.addReceiver(participant);
          message.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
          message.setContent(Integer.toString(currentPrice));
          message.setReplyWith(cfpReplyWithIds.get(index++));
          message.setConversationId(conversationId);
          myAgent.send(message);
      }
    }
  };

  private final ReceiveAllReplys receiveProposals;

  private final Behaviour respondToProposals = new OneShotBehaviour() {
    public int onend = 0;

    @Override
    public void action() {
      final Optional<ACLMessage> firstProposal = receiveProposals.getResponses().stream()
          .filter(message -> message.getPerformative() == ACLMessage.PROPOSE)
          .findFirst();
      
      if (firstProposal.isPresent()) {
        final ACLMessage proposalMessage = firstProposal.get();
        onend = 1;
        soldTo = proposalMessage.getSender();
        final ACLMessage reply = proposalMessage.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        reply.addReceiver(proposalMessage.getSender());
        myAgent.send(reply);
      }

      currentPrice = Math.max(currentPrice - priceDecrement, stopPrice);

      receiveProposals.getResponses().stream()
          .filter(message -> !firstProposal.isPresent() || message != firstProposal.get())
          .forEach(message -> {
            final ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
            reply.addReceiver(message.getSender());
            myAgent.send(reply);
          });

      if (currentPrice == stopPrice && !firstProposal.isPresent()) {
        onend = 1;
      }
    }
    
    @Override
    public int onEnd() {
      return onend;
    }
  };

  public Behaviour end = new OneShotBehaviour(myAgent) {
    @Override
    public void action() {
      participants.stream()
          .forEach(participant -> {
            final ACLMessage auctionEnded = new ACLMessage(ACLMessage.INFORM);
            auctionEnded.addReceiver(participant);
            auctionEnded.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
            auctionEnded.setContent("auction_ended");
            myAgent.send(auctionEnded);
          });

      if (soldTo != null) {
        System.out.println(String.format("%s: Auction for item %s ended. Sold for %s to %s", myAgent.getLocalName(), itemId, currentPrice, soldTo.getLocalName()));
      } else {
        System.out.println(String.format("%s: Auction for item %s ended. Not sold at stop price %s", myAgent.getLocalName(), itemId, currentPrice));
        System.out.println("Auction for item " + itemId + " ended. Not sold at stop price " + currentPrice);
      }
    }
  };
}
