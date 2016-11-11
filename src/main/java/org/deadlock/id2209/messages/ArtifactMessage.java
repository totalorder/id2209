package org.deadlock.id2209.messages;

import org.deadlock.id2209.model.Artifact;

import java.io.Serializable;

public class ArtifactMessage implements Serializable {
  public final Artifact artifact;

  public ArtifactMessage(final Artifact artifact) {
    this.artifact = artifact;
  }

  public static ArtifactMessage create(final Artifact artifact) {
    return new ArtifactMessage(artifact);
  }
}
