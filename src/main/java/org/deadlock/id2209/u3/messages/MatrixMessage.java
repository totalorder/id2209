package org.deadlock.id2209.u3.messages;

import jade.core.AID;
import org.deadlock.id2209.u3.MatrixUtil;

import java.io.Serializable;

public class MatrixMessage implements Serializable {
  public final AID[][] matrix = new AID[8][8];
  private final int numQueens;

  public MatrixMessage(AID[][] matrix) {
    numQueens = MatrixUtil.countNonNulls(matrix);

    for (int column = 0; column < 8; column++) {
      System.arraycopy(matrix[column], 0, this.matrix[column], 0, 8);
    }
  }

  public String toString() {
    return String.format("MatrixMessage{numQueens=%s}", numQueens);
  }
}
