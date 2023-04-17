package org.magcube.littlefactory.exception;

public class NumOfPlayersException extends Exception {

  public NumOfPlayersException(int numOfPlayers) {
    super("Number of players must be between 2 and 4. Number of players: " + numOfPlayers);
  }
}
