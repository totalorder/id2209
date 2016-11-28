package org.deadlock.id2209.u3;

import jade.core.AID;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixUtilTest {

  @Test
  public void testIsValid() throws Exception {
    final AID[][] matrix = new AID[8][8];

    matrix[6][2] = new AID("asd");

    MatrixUtil.printMatrix(matrix);
    assertFalse(MatrixUtil.isValid(matrix, 6, 1));
    assertFalse(MatrixUtil.isValid(matrix, 6, 3));
    assertFalse(MatrixUtil.isValid(matrix, 5, 2));
    assertFalse(MatrixUtil.isValid(matrix, 7, 2));
    assertFalse(MatrixUtil.isValid(matrix, 5, 1));
    assertFalse(MatrixUtil.isValid(matrix, 7, 1));
    assertFalse(MatrixUtil.isValid(matrix, 5, 3));
    assertFalse(MatrixUtil.isValid(matrix, 7, 3));
  }

  @Test
  public void testIsValid2() throws Exception {
    final AID[][] matrix = new AID[8][8];

    matrix[2][7] = new AID("asd");

    MatrixUtil.printMatrix(matrix);
    assertTrue(MatrixUtil.isValid(matrix, 0, 6));
  }
}