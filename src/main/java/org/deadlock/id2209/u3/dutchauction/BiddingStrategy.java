package org.deadlock.id2209.u3.dutchauction;

import java.io.Serializable;

/**
 * A bidding strategy that decides to buy or not
 */
public interface BiddingStrategy extends Serializable {
  boolean buy(int round, int startPrice, int currentPrice);
}
