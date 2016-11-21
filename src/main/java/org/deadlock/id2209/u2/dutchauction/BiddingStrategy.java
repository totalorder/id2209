package org.deadlock.id2209.u2.dutchauction;

public interface BiddingStrategy {
  boolean buy(int round, int startPrice, int currentPrice);
}
