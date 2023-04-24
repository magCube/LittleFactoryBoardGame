package org.magcube.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;

@Getter
public class Player {

  public static final int maxNumOfResourceCard = 7;

  private final ArrayList<ResourceCard> resources = new ArrayList<>();
  private final ArrayList<BuildingCard> buildings = new ArrayList<>();
  private final ArrayList<BuildingCard> activatedBuildings = new ArrayList<>();
  private final String id;
  private final String name;
  private int coin;
  private int pointTokens;

  @Builder
  public Player(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public List<Card> getResources() {
    return Collections.unmodifiableList(this.resources);
  }

  public List<Card> getBuildings() {
    return Collections.unmodifiableList(this.buildings);
  }

  public int points() {
    var buildingPoints = this.buildings.stream().mapToInt(BuildingCard::getPoints).sum();
    return pointTokens + buildingPoints;
  }

  public boolean willExceedMaxNumOfResourceCard(int take, int discard) {
    return resources.size() + take - discard > maxNumOfResourceCard;
  }

  public boolean willExceedMaxNumOfResourceCard(int take) {
    return resources.size() + take > maxNumOfResourceCard;
  }

  public void takeCards(HashMap<CardType, List<? extends Card>> categorizedCards) {
    for (var entry : categorizedCards.entrySet()) {
      if (entry.getKey() == CardType.BUILDING) {
        takeBuildingCards((List<BuildingCard>) entry.getValue());
      } else {
        takeResourceCards((List<ResourceCard>) entry.getValue());
      }
    }
  }

  public void takeResourceCards(List<ResourceCard> cards) {
    this.resources.addAll(cards);
  }

  public void takeBuildingCards(List<BuildingCard> cards) {
    this.buildings.addAll(cards);
  }

  public void discardCards(List<ResourceCard> cards) {
    this.resources.removeAll(cards);
  }

  public void receiveCoin(int coin) {
    this.coin = coin;
  }

  public void spendCoin() {
    coin = 0;
  }

  public void addPoints(int points) {
    this.pointTokens += points;
  }

  @Nullable
  public List<ResourceCard> equivalentResources(List<CardIdentity> cardIdentities) {
    var resourcesClone = new ArrayList<>(resources);
    var equivalentCards = new ArrayList<ResourceCard>();
    for (var cardIdentity : cardIdentities) {
      var cardInClone = resourcesClone.stream().filter(x -> x.isIdentical(cardIdentity)).findFirst();
      if (cardInClone.isEmpty()) {
        return null;
      } else {
        var card = cardInClone.get();
        resourcesClone.remove(card);
        equivalentCards.add(card);
      }
    }

    return equivalentCards;
  }

  @Nullable
  public BuildingCard equivalentBuilding(CardIdentity cardIdentity) {
    return buildings.stream().filter(x -> x.isIdentical(cardIdentity)).findFirst().orElse(null);
  }

  public void resetActivatedBuildings() {
    activatedBuildings.clear();
  }

  public void activateBuildings(BuildingCard buildingCard) {
    activatedBuildings.add(buildingCard);
  }
}
