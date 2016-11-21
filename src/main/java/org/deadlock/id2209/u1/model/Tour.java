package org.deadlock.id2209.u1.model;

import java.io.Serializable;
import java.util.List;

public class Tour implements Serializable {
  public final List<Integer> artifactIds;

  public Tour(final List<Integer> artifactIds) {
    this.artifactIds = artifactIds;
  }
}
