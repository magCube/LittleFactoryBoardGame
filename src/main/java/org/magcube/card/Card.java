package org.magcube.card;

import lombok.Getter;

@Getter
public abstract class Card {

  protected final CardType cardType;
  protected final int typeId;
  protected final String name;
  protected final int value;

  public Card(CardType cardType, int typeId, String name, int value) {
    this.cardType = cardType;
    this.typeId = typeId;
    this.name = name;
    this.value = value;
  }

  public boolean sameCard(Card card) {
    if (this == card) {
      return true;
    }
    if (card == null) {
      return false;
    }
    return cardType != null && cardType == card.cardType && typeId == card.typeId;
  }
}
