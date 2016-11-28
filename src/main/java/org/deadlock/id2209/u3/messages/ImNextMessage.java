package org.deadlock.id2209.u3.messages;


import java.io.Serializable;

public class ImNextMessage implements Serializable {
  public final boolean dummy = true;

  public ImNextMessage() {
  }

  public String toString() {
    return String.format("ImNextMessage{}");
  }
}

