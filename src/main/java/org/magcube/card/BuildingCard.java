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
public class BuildingCard extends Card {

  private CardIdentity[][] cost;
  private int points;
  private CardIdentity[][] effectCost;
  private CardIdentity[] effectCapital;
  private CardIdentity effectProduct;
  private int effectPoints;
  private Boolean isStartingBuilding;
  private Boolean isExtension;
  // todo
  private String specialEffect;

  @Builder
  public BuildingCard(CardIdentity cardIdentity, String name, int value, CardIdentity[][] cost, int points, CardIdentity[][] effectCost,
      CardIdentity[] effectCapital, CardIdentity effectProduct, int effectPoints, Boolean isStartingBuilding, Boolean isExtension,
      String specialEffect) {
    super(cardIdentity, name, value);
    this.cost = cost;
    this.points = points;
    this.effectCost = effectCost;
    this.effectCapital = effectCapital;
    this.effectProduct = effectProduct;
    this.effectPoints = effectPoints;
    this.isStartingBuilding = isStartingBuilding;
    this.isExtension = isExtension;
    this.specialEffect = specialEffect;
  }

  public boolean costMatch(List<CardIdentity> cardIdentities) {
    return Cards.isTwoDimCardIdentitiesMatch(cost, cardIdentities.toArray(new CardIdentity[0]));
  }

  public boolean effectCostMatch(List<CardIdentity> cardIdentities) {
    return Cards.isTwoDimCardIdentitiesMatch(effectCost, cardIdentities.toArray(new CardIdentity[0]));
  }

  public boolean effectCapitalMatch(List<CardIdentity> cardIdentities) {
    return Cards.isOneDimCardIdentitiesMatch(effectCapital, cardIdentities.toArray(new CardIdentity[0]));
  }
}
