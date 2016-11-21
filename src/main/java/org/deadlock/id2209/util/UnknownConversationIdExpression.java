package org.deadlock.id2209.util;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

public class UnknownConversationIdExpression implements MessageTemplate.MatchExpression {
  private Set<String> knownConversationIds = new HashSet<>();

  @Override
  public boolean match(ACLMessage msg) {
    final boolean known = knownConversationIds.contains(msg.getConversationId());
    knownConversationIds.add(msg.getConversationId());
    return !known;
  }

  public static MessageTemplate createTemplate() {
    return new MessageTemplate(new UnknownConversationIdExpression());
  }
}
