package org.deadlock.id2209.u3.dutchauction;

/**
 * Buy whenever the price is 90% or lower than the starting price
 */
public class HighBidderStrategy implements BiddingStrategy {

  @Override
  public boolean buy(int round, int startPrice, int currentPrice) {
    return (float)currentPrice / startPrice < 0.9f;
  }
}
