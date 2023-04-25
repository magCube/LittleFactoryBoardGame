package org.magcube.card;

public record CardIdentity(CardType cardType, int typeId) implements Comparable<CardIdentity> {

  @Override
  public int compareTo(CardIdentity o) {
    int compareByCardType = this.cardType.compareTo(o.cardType);
    return compareByCardType == 0 ? Integer.compare(this.typeId, o.typeId) : compareByCardType;
  }
}
