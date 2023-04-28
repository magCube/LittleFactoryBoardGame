package org.magcube.exception;

import org.magcube.enums.InvalidTradingMsg;

public class InvalidTradingException extends Exception {

  public InvalidTradingException(InvalidTradingMsg msg) {
    super(msg.msg());
  }
}
