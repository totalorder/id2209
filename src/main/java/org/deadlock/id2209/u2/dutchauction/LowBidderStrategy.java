package org.deadlock.id2209.u2.dutchauction;

/**
 * Buy whenever the price is 80% or lower than the starting price
 */
public class LowBidderStrategy implements BiddingStrategy {

  @Override
  public boolean buy(int round, int startPrice, int currentPrice) {
    return (float)currentPrice / startPrice < 0.8f;
  }
}
