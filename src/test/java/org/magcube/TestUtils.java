package org.magcube;

import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.magcube.card.Card;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class TestUtils {

  public static Stream<DisplayingPile<? extends Card>> provideBasicResourcesPiles()
      throws DisplayPileException {
    return Stream.of(getBasicResourcesPile(), getTestingBasicResourcesPile());
  }

  public static DisplayingPile<ResourceCard> getBasicResourcesPile()
      throws DisplayPileException {
    return new DisplayingPile<>(Main.BASIC_RESOURCES);
  }

  public static DisplayingPile<ResourceCard> getTestingBasicResourcesPile()
      throws DisplayPileException {
    var firstTierResources = new ArrayList<ResourceCard>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          ResourceCard.builder()
              .cardType(CardType.BASIC_RESOURCE)
              .typeId(i % 5)
              .value(1)
              .name(String.valueOf(i))
              .build()
      );
    }
    return new DisplayingPile<>(firstTierResources);
  }
}
