package org.magcube.littlefactory.card;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ResourceCard extends Card {

  private CardIdentity[][] cost;
  private CardIdentity[] capital;

  public ResourceCard() {
    super();
  }

  @Builder
  public ResourceCard(CardIdentity cardIdentity, String name, int value, CardIdentity[][] cost, CardIdentity[] capital) {
    super(cardIdentity, name, value);
    this.cost = cost;
    this.capital = capital;
  }
}
