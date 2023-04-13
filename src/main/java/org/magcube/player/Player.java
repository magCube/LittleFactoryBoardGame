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
    return isOwnCard(card) || isOwnFactory(card);
  }

  private boolean isOwnCard(Card card) {
    return resources.stream().anyMatch(x -> x.sameCard(card));
  }

  private boolean isOwnFactory(Card card) {
    return buildings.stream().anyMatch(x -> x.sameCard(card));
  }
}
