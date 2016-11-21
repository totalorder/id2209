package org.deadlock.id2209.u1.messages;

import org.deadlock.id2209.u1.model.Profile;

import java.io.Serializable;

public class RequestTourMessage implements Serializable {
  public final Profile profile;

  public RequestTourMessage(final Profile profile) {
    this.profile = profile;
  }

  public static RequestTourMessage create(final Profile profile) {
    return new RequestTourMessage(profile);
  }
}
