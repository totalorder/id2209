package org.deadlock.id2209.messages;

import java.io.Serializable;

public class RequestArtifactMessage implements Serializable {
  public final int artifactId;

  public RequestArtifactMessage(final int artifactId) {
    this.artifactId = artifactId;
  }

  public static RequestArtifactMessage create(final int artifactId) {
    return new RequestArtifactMessage(artifactId);
  }

  @Override
  public String toString() {
    return String.format("RequestArtifactMessage{artifactId=%s}", artifactId);
  }
}
