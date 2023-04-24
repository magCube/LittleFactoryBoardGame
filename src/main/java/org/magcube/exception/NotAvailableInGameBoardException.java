package org.magcube.exception;

public class NotAvailableInGameBoardException extends Exception {

  public NotAvailableInGameBoardException() {
    super("Cards are not available in game board.");
  }
}
