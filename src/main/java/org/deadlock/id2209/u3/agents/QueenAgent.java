package org.deadlock.id2209.u3.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.deadlock.id2209.u3.MatrixUtil;
import org.deadlock.id2209.u3.messages.ImNextMessage;
import org.deadlock.id2209.u3.messages.MoveMessage;
import org.deadlock.id2209.u3.messages.PositionMessage;
import org.deadlock.id2209.util.ReceiveObjectBehavior;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueenAgent extends Agent {
  private AID[][] matrix = new AID[8][8];
  private int x;
  private int y;
  private boolean firstQueen = false;
  private boolean lastQueen = false;
  private AID previous;
  private AID next;
  private Random random = new Random();
  private boolean solved = false;

  protected void setup() {
    final Object[] arguments = getArguments();
    y = (int)arguments[0];
    previous = (AID)arguments[1];
    x = random.nextInt(8);
    firstQueen = (boolean)arguments[2];
    lastQueen = (boolean)arguments[3];


    log("x: " + x + ", y: " + y + ", previous: " + previous + ", firstQueen: " + firstQueen + ", lastQueen: " + lastQueen);
    if (lastQueen) {
      addBehaviour(notifyPrevious);
    }

    addBehaviour(receiveImNext);
    addBehaviour(receivePosition);
    addBehaviour(receiveMove);

  }

  private Behaviour notifyPrevious = new OneShotBehaviour() {
    @Override
    public void action() {
      send(previous, new ImNextMessage());
    }
  };

  private Behaviour receiveImNext = new ReceiveObjectBehavior<ImNextMessage>(this, ImNextMessage.class) {
    @Override
    public void onObjectReceived(ACLMessage message, ImNextMessage imNextMessage) {
      next = message.getSender();
      if (previous != null) {
        send(previous, new ImNextMessage());
      } else {
        MatrixUtil.updatePosition(matrix, getAID(), x, y);
        send(next, new PositionMessage(x, y, matrix));
      }
    }
  };

  private int findValidPosition() {
    MatrixUtil.clearAid(matrix, getAID());
    final List<Integer> positions = IntStream.range(0, 8).boxed().collect(Collectors.toList());
    Collections.shuffle(positions);
    for (final int x : positions) {
      if (MatrixUtil.isValid(matrix, x, y)) {
        return x;
      }
    }

    return -1;
  }

  private Behaviour receivePosition = new ReceiveObjectBehavior<PositionMessage>(this, PositionMessage.class) {
    @Override
    public void onObjectReceived(ACLMessage message, PositionMessage positionMessage) {
      matrix = positionMessage.matrix;
      MatrixUtil.updatePosition(matrix, myAgent.getAID(), x, y);

      final int position = findValidPosition();
      if (position != -1) {
        x = position;
        MatrixUtil.updatePosition(matrix, getAID(), x, y);
        if (!lastQueen) {
          send(next, new PositionMessage(x, y, matrix));
        } else {
          log("Solution found!");
          MatrixUtil.printMatrix(matrix);
        }
      } else {
        send(previous, new MoveMessage());
      }

    }
  };

  private Behaviour receiveMove = new ReceiveObjectBehavior<MoveMessage>(this, MoveMessage.class) {
    @Override
    public void onObjectReceived(ACLMessage message, MoveMessage moveMessage) {
      final List<Integer> positions = IntStream.range(0, 8).boxed().collect(Collectors.toList());
      positions.remove((Integer)x);
      Collections.shuffle(positions);
      x = positions.get(0);


      MatrixUtil.clearAid(matrix, getAID());
      if (!MatrixUtil.isValid(matrix, x, y)) {
        MatrixUtil.updatePosition(matrix, myAgent.getAID(), x, y);
        send(previous, new MoveMessage());
      } else {
        MatrixUtil.updatePosition(matrix, myAgent.getAID(), x, y);
        send(next, new PositionMessage(x, y, matrix));
      }
    }
  };

  private void log(final String message) {
    System.out.println(getLocalName() + ": " + message);
  }

  private void send(final AID receiver, final Serializable object) {
    final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
    message.addReceiver(receiver);
    try {
      message.setContentObject(object);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    send(message);
  }
}
