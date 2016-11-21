package org.deadlock.id2209.util;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class ReceiveAllReplys extends ParallelBehaviour {
  private final List<ACLMessage> responses = new LinkedList<>();
  private final String protocol;
  private final String conversationId;
  private final List<String> replyToIds;

  public ReceiveAllReplys(final Agent agent,
                          final String protocol,
                          final String conversationId,
                          final List<String> replyToIds) {
    super(agent, ParallelBehaviour.WHEN_ALL);

    this.protocol = protocol;
    this.conversationId = conversationId;
    this.replyToIds = replyToIds;
  }

  @Override
  public void onStart() {
    final Instant start = Instant.now();
    replyToIds.stream().forEach(replyToId ->
            this.addSubBehaviour(
                new SimpleBehaviour(myAgent) {
                  boolean receivedMessage = false;
                  
                  @Override
                  public void action() {
                    final MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.and(
                            MessageTemplate.MatchProtocol(protocol),
                            MessageTemplate.MatchConversationId(conversationId)),
                        MessageTemplate.MatchInReplyTo(replyToId));

                    final ACLMessage message = myAgent.receive(messageTemplate);
                    if (message != null) {
                      receivedMessage = true;
                      responses.add(message);
                    }
                  }

                  @Override
                  public boolean done() {
                    return receivedMessage || Instant.now().isAfter(start.plus(Duration.ofSeconds(1)));
                  }
                })
    );
  }

  public List<ACLMessage> getResponses() {
    return responses;
  }
}
