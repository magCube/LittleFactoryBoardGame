package org.magcube.exception;

public class AlreadyTradedOrProducedException extends Exception {

  public AlreadyTradedOrProducedException() {
    super("Player already traded or produced.");
  }
}
