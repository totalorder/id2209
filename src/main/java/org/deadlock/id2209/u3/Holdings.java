package org.deadlock.id2209.u3;

import java.io.Serializable;

/**
 * Track the current holdings of an actor
 * Funds can be reserved before placing a bid, to make sure that no concurrent bidding exceeds the available funds
 * When a bid is won or lost, the purchase is confirmed, or the reserved amount if freed, respectively
 */
public class Holdings implements Serializable {
  private int funds;
  private int reserved = 0;
  private int purchases = 0;

  public Holdings(final int funds) {
    this.funds = funds;
  }

  /**
   * Reserve amount if possible.
   * Returns true if funds were reserved, false if not enough capital is available
   */
  public boolean reserve(final int amount) {
    if (funds - (reserved + amount) >= 0) {
      reserved = reserved + amount;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Release previously reserved capital so that can be used in another transaction
   */
  public void release(final int amount) {
    reserved = reserved - amount;
    if (reserved < 0) {
      throw new RuntimeException("Invalid release!");
    }
  }

  /**
   * Confirm a transaction by releasing the reserved funds, and withdrawing then from the account
   * Increments the number of purchases by one
   */
  public void confirm(final int amount) {
    release(amount);
    funds = funds - amount;
    purchases++;
  }

  public int getFunds() {
    return funds;
  }

  public int getPurchases() {
    return purchases;
  }

  public String getDescription() {
    return String.format("Holdings: Purchases: %s, Available: %s,  Reserved: %s, Total: %s",
        purchases, funds - reserved, reserved, funds);
  }
}
