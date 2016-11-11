package org.deadlock.id2209.util;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class DFRegistry {
  private final Agent agent;

  public DFRegistry(final Agent agent) {
    this.agent = agent;
  }

  public void registerService(final String type) {
    final ServiceDescription serviceDescription = new ServiceDescription();
    serviceDescription.setType(type);
    serviceDescription.setName(agent.getLocalName());

    final DFAgentDescription dfAgentDescription = new DFAgentDescription();
    dfAgentDescription.setName(agent.getAID());
    dfAgentDescription.addServices(serviceDescription);

    try {
      DFService.register(agent, dfAgentDescription);
    } catch (FIPAException e) {
      throw new RuntimeException(e);
    }
  }

  public AID findService(final String service) {
    final DFAgentDescription dfAgentDescription = new DFAgentDescription();
    final ServiceDescription serviceDescription = new ServiceDescription();
    serviceDescription.setType(service);
    dfAgentDescription.addServices(serviceDescription);
    try {
      final DFAgentDescription[] dfAgentDescriptions = DFService.search(agent, dfAgentDescription);
      if (dfAgentDescriptions.length>0)
        return dfAgentDescriptions[0].getName();
    } catch (FIPAException e) {
      throw new RuntimeException(e);
    }

    return null;
  }
}
