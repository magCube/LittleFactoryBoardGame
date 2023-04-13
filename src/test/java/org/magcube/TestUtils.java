package org.magcube;

import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.magcube.card.Card;
import org.magcube.card.ResourceCard;
import org.magcube.card.displayingpile.DisplayingPile;
import org.magcube.exception.DisplayPileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class TestUtils {

  public static Stream<DisplayingPile<? extends Card>> provideFirstTierResourcesPiles()
      throws DisplayPileException {
    return Stream.of(
        getFirstTierResourcesPile(),
        getTestingFirstTierResourcesPile()
    );
  }

  public static DisplayingPile<ResourceCard> getFirstTierResourcesPile()
      throws DisplayPileException {
    return new DisplayingPile<>(Main.BASIC_RESOURCES);
  }

  public static DisplayingPile<ResourceCard> getTestingFirstTierResourcesPile()
      throws DisplayPileException {
    var firstTierResources = new ArrayList<ResourceCard>();
    for (var i = 0; i < 10; i++) {
      firstTierResources.add(
          ResourceCard.builder().value(1).name(String.valueOf(i))
              .typeId(i % 5)
              .build()
      );
    }
    return new DisplayingPile<>(firstTierResources);
  }
}
