package org.deadlock.id2209.model;

import java.io.Serializable;

public class Profile implements Serializable {
  public final int age;
  public String occupation;
  public final String gender;
  public final String interest;

  public Profile(final int age, final String occupation, final String gender, final String interest) {
    this.age = age;
    this.occupation = occupation;
    this.gender = gender;
    this.interest = interest;
  }
}
