package org.magcube.exception;

public class InvalidTradingException extends Exception {

  public InvalidTradingException(InvalidTradingMsg msg) {
    super(msg.msg());
  }
}
