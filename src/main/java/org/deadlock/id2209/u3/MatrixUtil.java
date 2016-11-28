package org.deadlock.id2209.u3;

import jade.core.AID;

public class MatrixUtil {
  public static int countNonNulls(final AID[][] matrix) {
    int nonNulls = 0;
    for (int column = 0; column < 8; column++) {
      for (int row = 0; row < 8; row++) {
        if (matrix[column][row] != null) {
          nonNulls++;
        }
      }
    }
    return nonNulls;
  }

  public static void clearAid(final AID[][] matrix, final AID aid) {
    for (int column = 0; column < 8; column++) {
      for (int row = 0; row < 8; row++) {
        final AID currentAid = matrix[column][row];
        if (aid.equals(currentAid)) {
          matrix[column][row] = null;
        }
      }
    }
  }

  public static void updatePosition(final AID[][] matrix, final AID aid, final int x, final int y) {
    clearAid(matrix, aid);


    matrix[x][y] = aid;
  }

  public static void printMatrix(final AID[][] matrix) {
    final StringBuilder sb = new StringBuilder();

    for (int row = 0; row < 8; row++) {
      for (int column = 0; column < 8; column++) {
        final AID aid = matrix[column][row];
        sb.append('|');
        sb.append(' ');
        if (aid == null) {
          sb.append(' ');
        } else {
          sb.append(aid.getLocalName().charAt(aid.getLocalName().length() - 1));
        }
        sb.append(' ');
      }
      sb.append('\n');
      for (int column = 0; column < 8; column++) {
        sb.append('+');
        sb.append("---");
      }
      sb.append('\n');
    }

    System.out.println(sb.toString());
  }

  public static boolean isValid(final AID[][] matrix, final int x, final int y) {
    for (int distance = 1; distance < 8; distance++) {
      // Search outward from x, y
      final int leftX = x - distance;
      final int rightX = x + distance;
      final int topY =y - distance;
      final int bottomY = y + distance;

      // Check column
      if (topY >= 0 && matrix[x][topY] != null || bottomY <=7 && matrix[x][bottomY] != null) {
        return false;
      }

      // Check row
      if (leftX >=0 && matrix[leftX][y] != null || rightX <=7 && matrix[rightX][y] != null) {
        return false;
      }

      // Check left/top -> right/bottom
      if (leftX >=0 && topY >= 0 && matrix[leftX][topY] != null || rightX <=7 && bottomY <=7 && matrix[rightX][bottomY] != null) {
        return false;
      }

      // Check right/top -> lef/bottom
      if (rightX <=7 && topY >= 0 && matrix[rightX][topY] != null || leftX >=0 && bottomY <=7 && matrix[leftX][bottomY] != null) {
        return false;
      }
    }
    return true;
  }
}
