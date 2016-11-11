package org.deadlock.id2209.model;

import java.io.Serializable;

public class Artifact implements Serializable {
  public final int id;
  public final String name;
  public final String creator;
  public final int created;
  public final String placeOfCreation;
  public final String genre;

  public Artifact(final int id,
                  final String name,
                  final String creator,
                  final int created,
                  final String placeOfCreation,
                  final String genre) {
    this.id = id;
    this.name = name;
    this.creator = creator;
    this.created = created;
    this.placeOfCreation = placeOfCreation;
    this.genre = genre;
  }
}
