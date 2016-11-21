package org.deadlock.id2209.u2.dutchauction;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.deadlock.id2209.u2.Holdings;
import org.deadlock.id2209.util.UnknownConversationIdExpression;

/**
 * Implement a dutch auction buyer, with a specified buying strategy and available holdings
 */
public class DutchAuctionResponder extends CyclicBehaviour {

  private final BiddingStrategy strategy;
  private final Holdings holdings;
  private final MessageTemplate unknownConversationTemplate;

  public DutchAuctionResponder(final Agent agent, final BiddingStrategy strategy, final Holdings holdings) {
    super(agent);
    this.strategy = strategy;
    this.holdings = holdings;
    this.unknownConversationTemplate = UnknownConversationIdExpression.createTemplate();
  }

  public void printHoldings() {
    System.out.println(String.format("%s: %s", myAgent.getLocalName(), holdings.getDescription()));
  }

  /**
   * Receive INFORM messages with unknown conversation ids, and track a separate auction for each of them
   */
  @Override
  public void action() {
    final MessageTemplate messageTemplate = MessageTemplate.and(
        MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION),
            MessageTemplate.MatchPerformative(ACLMessage.INFORM)), unknownConversationTemplate);

    final ACLMessage message = myAgent.receive(messageTemplate);
    if (message != null) {
      myAgent.addBehaviour(new DutchAuctionBehavior(myAgent, message, strategy, holdings));
    } else {
      block();
    }
  }

  /**
   * Track a specific auction, using strategy to decide when to bid
   */
  class DutchAuctionBehavior extends ParallelBehaviour {
    private final String conversationId;
    private final String itemId;
    private final BiddingStrategy strategy;
    private final Holdings holdings;
    private int reservedAmount = 0;
    private int round = 1;
    private int startPrice = -1;

    public DutchAuctionBehavior(final Agent agent,
                                final ACLMessage message,
                                final BiddingStrategy strategy,
                                final Holdings holdings) {
      super(agent, ParallelBehaviour.WHEN_ALL);
      this.strategy = strategy;
      this.holdings = holdings;
      this.conversationId = message.getConversationId();
      this.itemId = message.getContent();

      printHoldings();
      bid.addSubBehaviour(receiveCfp);
      bid.addSubBehaviour(receiveProposalResponse);
      addSubBehaviour(bid);
      addSubBehaviour(receiveAuctionEnded);
    }

    /**
     * Receive CFPs (call for proposals) and maybe send proposal, then wait for the answer to proposal if sent
     */
    private SequentialBehaviour bid = new SequentialBehaviour(myAgent) {
    };

    /**
     * Receive CFPs (call for proposals) and maybe send proposal depending on the decision of the strategy
     */
    private Behaviour receiveCfp = new SimpleBehaviour() {
      boolean done = false;
      @Override
      public void action() {
        final MessageTemplate messageTemplate = MessageTemplate.and(
            MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION),
                MessageTemplate.MatchConversationId(conversationId)),
            MessageTemplate.MatchPerformative(ACLMessage.CFP));

        final ACLMessage message = myAgent.receive(messageTemplate);
        if (message != null) {
          final int currentPrice = Integer.parseInt(message.getContent());

          // Record the starting price on the first round
          if (round == 1) {
            startPrice = currentPrice;
          }

          round++;

          // Ask the bidding strategy whether to buy or not
          final boolean buy = strategy.buy(round, startPrice, currentPrice);

          // If the strategy decides to buy, try to reserve that amount and place a bid
          if (buy && holdings.reserve(currentPrice)) {
            System.out.println(String.format("%s: Bid on item %s at %s", myAgent.getLocalName(), itemId, currentPrice));
            printHoldings();
            // Remember the amount reserved to confirm/release depending on if the bid is a win
            reservedAmount = currentPrice;

            // Send a PROPOSE to make a bid at the current price
            final ACLMessage reply = message.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            myAgent.send(reply);
            done = true;
          }
        } else {
          block();
        }
      }

      @Override
      public boolean done() {
        return done;
      }
    };

    private Behaviour receiveProposalResponse = new SimpleBehaviour(myAgent) {
      boolean done = false;

      @Override
      public void action() {
        final MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION),
                MessageTemplate.MatchConversationId(conversationId)),
            MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL))
        );

        final ACLMessage message = myAgent.receive(messageTemplate);
        if (message != null) {
          done = true;
          if (message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            // Confirm the purchase if the bid is won
            holdings.confirm(reservedAmount);
            System.out.println(String.format("%s: Won bid on item %s at %s", myAgent.getLocalName(), itemId, reservedAmount));
            printHoldings();
          } else {
            // Release the reserved amount if the bid is lost
            holdings.release(reservedAmount);
            System.out.println(String.format("%s: Lost bid on item %s at %s", myAgent.getLocalName(), itemId, reservedAmount));
            printHoldings();
          }
        } else {
          block();
        }
      }

      @Override
      public boolean done() {
        return done;
      }
    };

    /**
     * Receive an INFORM with content auction_ended, which logs a message and completes
     */
    private Behaviour receiveAuctionEnded = new SimpleBehaviour(myAgent) {
      boolean done = false;

      @Override
      public void action() {
        final MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION),
                MessageTemplate.MatchConversationId(conversationId)),
            MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchContent("auction_ended"))
        );

        final ACLMessage message = myAgent.receive(messageTemplate);
        if (message != null) {
          done = true;
          System.out.println(String.format("%s: Auction for item %s ended", myAgent.getLocalName(), itemId));
        } else {
          block();
        }
      }

      @Override
      public boolean done() {
        return done;
      }
    };
  }
}
