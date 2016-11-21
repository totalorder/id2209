package org.deadlock.id2209.u2.dutchauction;

public class HighBidderStrategy implements BiddingStrategy {

  @Override
  public boolean buy(int round, int startPrice, int currentPrice) {
    return (float)currentPrice / startPrice < 0.9f;
  }
}
