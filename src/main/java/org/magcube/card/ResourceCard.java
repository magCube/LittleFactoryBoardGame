package org.magcube.card;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class ResourceCard extends Card {

  private CardIdentity[][] cost;
  private CardIdentity[] capital;

  @Builder
  public ResourceCard(CardIdentity cardIdentity, String name, int value, CardIdentity[][] cost, CardIdentity[] capital) {
    super(cardIdentity, name, value);
    this.cost = cost;
    this.capital = capital;
  }

  public boolean costMatch(List<CardIdentity> cardIdentities) {
    return Cards.isTwoDimCardIdentitiesMatch(cost, cardIdentities.toArray(new CardIdentity[0]));
  }

  public boolean capitalMatch(List<CardIdentity> cardIdentities) {
    return Cards.isOneDimCardIdentitiesMatch(capital, cardIdentities.toArray(new CardIdentity[0]));
  }
}
