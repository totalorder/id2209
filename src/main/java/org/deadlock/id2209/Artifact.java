package org.deadlock.id2209;

import java.io.Serializable;

public class Artifact implements Serializable {
  final int id;
  final String name;
  final String creator;
  final int created;
  final String placeOfCreation;
  final String genre;

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
