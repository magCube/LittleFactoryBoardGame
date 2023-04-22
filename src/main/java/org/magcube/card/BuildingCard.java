package org.magcube.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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

  public BuildingCard() {
  }

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

  // todo: move to Card
  public boolean costMatch(List<CardIdentity> checkingCardIdentities) {
    // the outer layer is OR, the inner layer is AND
    for (CardIdentity[] option : cost) {
      var optionList = new ArrayList<>(Arrays.asList(option));
      if (Cards.isCardIdentitiesSame(optionList, checkingCardIdentities)) {
        return true;
      }
    }
    return false;
  }

  public boolean effectCostMatch(List<CardIdentity> checkingCardIdentities) {
    // the outer layer is OR, the inner layer is AND
    for (CardIdentity[] option : effectCost) {
      var optionList = new ArrayList<>(Arrays.asList(option));
      if (Cards.isCardIdentitiesSame(optionList, checkingCardIdentities)) {
        return true;
      }
    }
    return false;
  }

  public boolean effectCapitalMatch(List<CardIdentity> checkingCardIdentities) {
    if (effectCapital.length == 0) {
      return false;
    }
    var capitalList = new ArrayList<>(Arrays.asList(effectCapital));
    return Cards.isCardIdentitiesSame(capitalList, checkingCardIdentities);
  }
}
