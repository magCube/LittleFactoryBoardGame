package org.magcube;

import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.magcube.card.Card;
import org.magcube.card.CardDeck;
import org.magcube.card.CardIdentity;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.NumOfPlayersException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class TestUtils {

  public static Stream<DisplayingPile<? extends Card>> provideBasicResourcesPiles()
      throws DisplayPileException, NumOfPlayersException {
    return Stream.of(getBasicResourcesPile(), getTestingBasicResourcesPile());
  }

  public static DisplayingPile<ResourceCard> getBasicResourcesPile()
      throws DisplayPileException, NumOfPlayersException {
    return new DisplayingPile<>(CardDeck.get(4).basicResource);
  }

  public static DisplayingPile<ResourceCard> getTestingBasicResourcesPile()
      throws DisplayPileException {
    var firstTierResources = new ArrayList<ResourceCard>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          ResourceCard.builder()
              .cardIdentity(new CardIdentity(CardType.BASIC_RESOURCE, i % 5))
              .value(1)
              .name(String.valueOf(i))
              .build()
      );
    }
    return new DisplayingPile<>(firstTierResources);
  }
}
