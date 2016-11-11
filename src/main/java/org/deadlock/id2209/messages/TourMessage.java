package org.deadlock.id2209.messages;

import org.deadlock.id2209.model.Tour;

import java.io.Serializable;

public class TourMessage implements Serializable {
  public final Tour tour;

  public TourMessage(final Tour tour) {
    this.tour = tour;
  }

  public static TourMessage create(final Tour tour) {
    return new TourMessage(tour);
  }
}
