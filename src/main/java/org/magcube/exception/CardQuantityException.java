package org.magcube.exception;

import org.magcube.enums.CardType;

public class CardQuantityException extends Exception {

  public CardQuantityException(CardType cardType, int typeId) {
    super(String.format("Card not found: cardType=%s, typeId=%d", cardType, typeId));
  }
}
