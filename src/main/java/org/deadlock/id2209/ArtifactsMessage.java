package org.deadlock.id2209;

import java.io.Serializable;
import java.util.Set;

public class ArtifactsMessage implements Serializable {
  public final Set<Artifact> artifacts;

  public ArtifactsMessage(final Set<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

  public static ArtifactsMessage create(final Set<Artifact> artifacts) {
    return new ArtifactsMessage(artifacts);
  }
}
