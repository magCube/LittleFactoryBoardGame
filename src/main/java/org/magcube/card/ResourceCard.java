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

  public boolean capitalMatch(List<CardIdentity> checkingCardIdentities) {
    if (capital.length == 0) {
      return false;
    }
    var capitalList = new ArrayList<>(Arrays.asList(capital));
    return Cards.isCardIdentitiesSame(capitalList, checkingCardIdentities);
  }
}
