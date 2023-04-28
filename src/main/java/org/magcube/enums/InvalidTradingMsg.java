package org.magcube.enums;

public enum InvalidTradingMsg {
  PAYMENT_NO_ENOUGH("Payment is not enough."),
  COST_NOT_MATCH("Cost is not match."),
  CAPITAL_NOT_MATCH("Capital is not match."),
  NOT_ONE_TO_N_OR_N_TO_ONE("Trading must be 1:n or n:1.");

  private final String msg;

  InvalidTradingMsg(String msg) {
    this.msg = msg;
  }

  public String msg() {
    return msg;
  }
}

