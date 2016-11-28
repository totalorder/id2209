package org.deadlock.id2209.u3;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.CloneAction;
import jade.domain.mobility.MobileAgentDescription;
import jade.domain.mobility.MobilityOntology;
import jade.domain.mobility.MoveAction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class AMSUtil {
  public static Map<String, Location> getLocations(Agent agent) {
    // Get available locations with AMS
    sendRequest(agent, new Action(agent.getAMS(), new QueryPlatformLocationsAction()));

    //Receive response from AMS
    MessageTemplate mt = MessageTemplate.and(
        MessageTemplate.MatchSender(agent.getAMS()),
        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
    ACLMessage resp = agent.blockingReceive(mt);
    ContentElement ce = null;
    try {
      ce = agent.getContentManager().extractContent(resp);
    } catch (Codec.CodecException | OntologyException e) {
      throw new RuntimeException(e);
    }
    Result result = (Result) ce;
    jade.util.leap.Iterator it = result.getItems().iterator();
    final Map<String, Location> locations = new HashMap<>();
    while (it.hasNext()) {
      Location loc = (Location)it.next();
      locations.put(loc.getName(), loc);
    }

    return locations;
  }

  public static void sendRequest(Agent agent, Action action) {
    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
    request.setLanguage(new SLCodec().getName());
    request.setOntology(MobilityOntology.getInstance().getName());
    try {
      agent.getContentManager().fillContent(request, action);
      request.addReceiver(action.getActor());
      agent.send(request);
    }
    catch (Exception ex) { ex.printStackTrace(); }
  }

  public static void clone(Agent agent, int clones, String destinationName) {
    final Map<String, Location> locations = AMSUtil.getLocations(agent);
    for (int i = 0; i < clones; i++) {
      Location dest = locations.get(destinationName);
      MobileAgentDescription mad = new MobileAgentDescription();
      mad.setName(agent.getAID());
      mad.setDestination(dest);
      String newName = agent.getLocalName() + "-" + destinationName + "-clone" + i;
      CloneAction ca = new CloneAction();
      ca.setNewName(newName);
      ca.setMobileAgentDescription(mad);
      System.out.println("Creating clone: " + newName + " in " + destinationName);
      AMSUtil.sendRequest(agent, new Action(agent.getAID(), ca));
    }
  }

  public static void move(Agent agent, String destinationName) {
    final Map<String, Location> locations = AMSUtil.getLocations(agent);
    MobileAgentDescription mad = new MobileAgentDescription();
    mad.setName(agent.getAID());
    mad.setDestination(locations.get(destinationName));
    MoveAction ma = new MoveAction();
    ma.setMobileAgentDescription(mad);
    AMSUtil.sendRequest(agent, new Action(agent.getAID(), ma));
  }

  public static Behaviour createMobilityReceiveBehavior(Agent agent) {
    return new CyclicBehaviour(agent) {
      public void action() {
        ACLMessage msg = agent.receive(MessageTemplate.MatchSender(agent.getAID()));

        if (msg == null) { block(); return; }

        if (msg.getPerformative() == ACLMessage.REQUEST){

          try {
            ContentElement content = agent.getContentManager().extractContent(msg);
            Concept concept = ((Action)content).getAction();

            if (concept instanceof CloneAction){

              CloneAction ca = (CloneAction)concept;
              String newName = ca.getNewName();
              Location l = ca.getMobileAgentDescription().getDestination();
              agent.doClone(l, newName);
            }
            else if (concept instanceof MoveAction){

              MoveAction ma = (MoveAction)concept;
              Location l = ma.getMobileAgentDescription().getDestination();
              agent.doMove(l);
            }
          }
          catch (Exception ex) { throw new RuntimeException(ex); }
        }
        else { System.out.println("Unexpected msg from controller agent"); }
      }
    };
  }
}
