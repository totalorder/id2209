package org.deadlock.id2209.u3.messages;

import jade.core.AID;
import org.deadlock.id2209.u3.MatrixUtil;

import java.io.Serializable;

public class PositionMessage implements Serializable {
  public final int x;
  public final int y;
  public final AID[][] matrix;

  public PositionMessage(final int x, final int y, final AID[][] matrix) {
    this.x = x;
    this.y = y;
    this.matrix = matrix;
  }

  public String toString() {
    return String.format("PositionMessage{x=%s,y=%s,numQueens=%s}", x, y, MatrixUtil.countNonNulls(matrix));
  }
}
