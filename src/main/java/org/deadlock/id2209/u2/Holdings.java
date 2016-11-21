package org.deadlock.id2209.u2;

public class Holdings {
  private int funds;
  private int reserved = 0;
  private int purchases = 0;

  public Holdings(final int funds) {
    this.funds = funds;
  }

  public boolean reserve(final int amount) {
    if (funds - (reserved + amount) >= 0) {
      reserved = reserved + amount;
      return true;
    } else {
      return false;
    }
  }

  public void release(final int amount) {
    reserved = reserved - amount;
    if (reserved < 0) {
      throw new RuntimeException("Invalid free!");
    }
  }

  public void buy(final int amount) {
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
