package org.deadlock.id2209.util;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.List;

public class ReceiveAllReplysAfterTimeout extends ParallelBehaviour {
  private ReceiveAllReplys receiveAllReplys;
  private final Agent agent;
  private final String protocol;
  private final String conversationId;
  private final List<String> replyToIds;

  public ReceiveAllReplysAfterTimeout(final Agent agent,
                                      final String protocol,
                                      final String conversationId,
                                      final List<String> replyToIds) {
    super(agent, ParallelBehaviour.WHEN_ALL);
    this.agent = agent;
    this.protocol = protocol;
    this.conversationId = conversationId;
    this.replyToIds = replyToIds;
  }

  @Override
  public void onStart() {
    System.out.println("On START ReceiveAllReplysAfterTimeout");
    this.receiveAllReplys = new ReceiveAllReplys(agent, protocol, conversationId, replyToIds);
    this.addSubBehaviour(receiveAllReplys);
    this.addSubBehaviour(new WakerBehaviour(agent, 1000) {
      @Override
      protected void onWake() {
        System.out.println("Wake up!");
      }
    });
  }

  @Override
  public int onEnd() {
    System.out.println("ReceiveAllReplysAfterTimeout onEnd");
    return 0;
  }

  public List<ACLMessage> getResponses() {
    return receiveAllReplys.getResponses();
  }
}
