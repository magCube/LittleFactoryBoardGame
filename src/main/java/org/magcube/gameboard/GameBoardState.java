package org.magcube.gameboard;

import org.magcube.card.BuildingCard;
import org.magcube.card.ResourceCard;
import org.magcube.displayingpile.PileState;

public record GameBoardState(PileState<ResourceCard> basicResource,
                             PileState<ResourceCard> levelOneResource,
                             PileState<ResourceCard> levelTwoResource,
                             PileState<BuildingCard> building) {

}
