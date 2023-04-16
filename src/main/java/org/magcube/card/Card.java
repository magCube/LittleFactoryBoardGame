package org.magcube.card;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Card {

  //TODO: refactor it to CardIdentity and add isIdentical()
  protected CardType cardType;
  protected int typeId;
  protected String name;
  protected int value;

  public Card() {
  }

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
