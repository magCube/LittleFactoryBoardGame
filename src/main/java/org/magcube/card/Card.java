package org.magcube.card;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class Card {

  protected CardIdentity cardIdentity;
  protected String name;
  protected int value;

  public Card() {
  }

  public Card(CardIdentity cardIdentity, String name, int value) {
    this.cardIdentity = cardIdentity;
    this.name = name;
    this.value = value;
  }

  // don't use getCardType as name as Jackson will parse it is a field
  public CardType cardType() {
    return cardIdentity.getCardType();
  }

  // don't use getTypeId as name as Jackson will parse it is a field
  public int typeId() {
    return cardIdentity.getTypeId();
  }

  public boolean isIdentical(CardIdentity cardIdentity) {
    return this.cardIdentity.equals(cardIdentity);
  }

  public boolean isIdentical(Card card) {
    return cardIdentity.equals(card.cardIdentity);
  }
}
