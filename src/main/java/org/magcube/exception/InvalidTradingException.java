package org.magcube.exception;

public class InvalidTradingException extends Exception {

  public static final String paymentNoEnough = "Payment is not enough.";
  public static final String costNotMatch = "Cost is not match.";
  public static final String capitalNotMatch = "Capital is not match.";
  public static final String notOneToNOrNToOne = "Trading must be 1:n or n:1.";


  public InvalidTradingException(String message) {
    super(message);
  }
}
