package org.deadlock.id2209.agents;

import jade.domain.FIPAAgentManagement.Property;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.deadlock.id2209.util.DFRegistry;

import java.util.List;
import java.util.Scanner;

/**
 * List the available services and ask the user to list the properties of a chosen one
 */
public class DFLookupAgent extends Agent {
  private final DFRegistry dfRegistry = new DFRegistry(this);

  protected void setup() {
    addBehaviour(askForUserInput);
  }

  private final Behaviour askForUserInput = new OneShotBehaviour(this) {
    @Override
    public void action() {

      final Scanner scanner = new Scanner(System.in);
      final List<String> services = dfRegistry.findServices();
      System.out.println("Pick service to get info");
      int index = 1;
      for (final String service : services) {
        System.out.println(index++ + ": " + service);
      }
      System.out.print("Choice: ");
      final int choice = scanner.nextInt();
      final String serviceName = services.get(choice - 1);
      System.out.print("Searching for: " + serviceName);
      final DFAgentDescription agentDescription = dfRegistry.findServiceDescription(serviceName);
      final ServiceDescription serviceDescription = (ServiceDescription)agentDescription.getAllServices().next();

      System.out.println("Name: " + serviceDescription.getName());
      System.out.println("Properties: ");
      serviceDescription.getAllProperties().forEachRemaining(object -> {
        final Property property = (Property)object;
        System.out.println("  " + property.getName() + "=" + property.getValue());
      });
    }
  };
}