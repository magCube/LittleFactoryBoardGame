package org.magcube.exception;

public class PlayerDoesNotOwnCardsException extends Exception {

  public PlayerDoesNotOwnCardsException(String cardType) {
    super("Player does not own the " + cardType + " cards.");
  }
  
}
