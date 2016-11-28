package org.deadlock.id2209.u3.messages;

import java.io.Serializable;

public class MoveMessage implements Serializable {
  public final boolean dummy = true;

  public MoveMessage() {
  }

  public String toString() {
    return String.format("Move{}");
  }
}
