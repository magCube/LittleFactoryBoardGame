package org.magcube.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.ResourceCard;

@Getter
public class Player {

  private final ArrayList<ResourceCard> resources = new ArrayList<>();
  private final ArrayList<BuildingCard> buildings = new ArrayList<>();
  private final String id;
  private final String name;
  @Setter
  private int coin;
  private int points;

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

  public int getPoints() {
    // todo: the buildings is not always one point
    return points + this.buildings.size();
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

  public void addPoints(int points) {
    if (points < 0) {
      System.out.println("Negative number is not expected in addPoints!");
      return;
    }
    this.points += points;
  }

  public boolean ownCard(Card card) {
    return isOwnResource(card) || isOwnBuilding(card);
  }

  public boolean isOwnResource(Card card) {
    return resources.stream().anyMatch(x -> x.isIdentical(card));
  }

  public boolean isOwnBuilding(Card card) {
    return buildings.stream().anyMatch(x -> x.isIdentical(card));
  }

  public boolean isOwnAllResources(List<ResourceCard> cards) {
    var resourcesClone = new ArrayList<>(this.resources);
    for (var card : cards) {
      var cardInClone = resourcesClone.stream().filter(x -> x.isIdentical(card)).findFirst();

      if (cardInClone.isEmpty()) {
        return false;
      } else {
        resourcesClone.remove(cardInClone.get());
      }
    }

    return true;
  }
}
