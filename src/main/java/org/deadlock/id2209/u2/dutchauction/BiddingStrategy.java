package org.deadlock.id2209.u2.dutchauction;

/**
 * A bidding strategy that decides to buy or not
 */
public interface BiddingStrategy {
  boolean buy(int round, int startPrice, int currentPrice);
}
