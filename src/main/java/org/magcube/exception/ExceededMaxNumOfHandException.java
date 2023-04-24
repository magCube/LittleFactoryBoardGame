package org.magcube.exception;

public class ExceededMaxNumOfHandException extends Exception {

  public ExceededMaxNumOfHandException() {
    super("Player will exceed max number of resource cards.");
  }
}
