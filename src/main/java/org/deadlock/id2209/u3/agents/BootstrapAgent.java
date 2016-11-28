package org.deadlock.id2209.u3.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class BootstrapAgent extends Agent {
  protected void setup() {
    final Object[] arguments = getArguments();
    if (arguments == null || arguments.length != 1) {
      throw new RuntimeException("Number of queens have to be specified as the first argument!");
    }
    try {
    final int numberOfQueens = Integer.parseInt((String)arguments[0]);
      AID previous = null;
      for (int i = 0; i < numberOfQueens; i++) {
        final AgentController newAgent = getContainerController().createNewAgent(
            "queen" + i,
            "org.deadlock.id2209.u3.agents.QueenAgent",
            new Object[]{i, previous, i == 0, i == numberOfQueens - 1});
        previous = new AID(newAgent.getName(), AID.ISGUID);
        newAgent.start();
      }
    } catch (StaleProxyException e) {
      throw new RuntimeException(e);
    }
  }
}
