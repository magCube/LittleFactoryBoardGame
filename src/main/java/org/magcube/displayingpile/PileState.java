package org.magcube.displayingpile;

import java.util.List;
import org.magcube.card.Card;
import org.magcube.enums.CardType;

public record PileState<T extends Card>(CardType cardType,
                                        List<List<T>> displaying,
                                        List<T> deck,
                                        List<T> discardPile) {

}
