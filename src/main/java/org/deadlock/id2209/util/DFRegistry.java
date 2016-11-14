package org.deadlock.id2209.util;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DFRegistry {
  private final Agent agent;

  public DFRegistry(final Agent agent) {
    this.agent = agent;
  }

  public void registerService(final String type, final Property... properties) {
    final ServiceDescription serviceDescription = new ServiceDescription();
    serviceDescription.setType(type);
    serviceDescription.setName(agent.getLocalName());
    for (final Property property : properties) {
      serviceDescription.addProperties(property);
    }

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

  public DFAgentDescription findServiceDescription(final String service) {
    final DFAgentDescription dfAgentDescription = new DFAgentDescription();
    final ServiceDescription serviceDescription = new ServiceDescription();
    serviceDescription.setType(service);
    dfAgentDescription.addServices(serviceDescription);
    try {
      final DFAgentDescription[] dfAgentDescriptions = DFService.search(agent, dfAgentDescription);
      if (dfAgentDescriptions.length > 0)
        return dfAgentDescriptions[0];
    } catch (FIPAException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<String> findServices() {
    final List<String> services = new LinkedList<>();
//    final DFAgentDescription dfAgentDescription = new DFAgentDescription();
//    final ServiceDescription serviceDescription = new ServiceDescription();
//    dfAgentDescription.addServices(serviceDescription);
    try {
      final DFAgentDescription[] dfAgentDescriptions = DFService.search(agent, new DFAgentDescription());
      Arrays.asList(dfAgentDescriptions).stream()
          .forEach(dfAgentDescription ->
              dfAgentDescription.getAllServices()
                  .forEachRemaining(serviceDescription ->
                      services.add(((ServiceDescription) serviceDescription).getType())));
      return services;
    } catch (FIPAException e) {
      throw new RuntimeException(e);
    }
  }
}
