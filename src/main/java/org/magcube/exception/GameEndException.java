package org.magcube.exception;

public class GameEndException extends Exception {

  public GameEndException() {
    super("Game is ended.");
  }
}
