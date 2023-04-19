package org.magcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.magcube.card.BuildingCard;
import org.magcube.card.Card;
import org.magcube.card.CardType;
import org.magcube.card.ResourceCard;
import org.magcube.exception.DisplayPileException;
import org.magcube.exception.GameStartupException;
import org.magcube.gameboard.GameBoard;
import org.magcube.player.NumOfPlayers;
import org.magcube.player.Player;

@Getter
public class GameInstance {

  private GameBoard gameBoard;
  private List<Player> players;
  private Player currentPlayer; // only accessible by this object itself, need to be thread safe
  private boolean isTraded;

  public GameInstance() {
    this.players = new ArrayList<>();
  }

  // todo: the following is temp implementation
  public void setPlayers(NumOfPlayers numOfPlayers) throws DisplayPileException {
    var _players = new ArrayList<Player>();
    for (var i = 1; i <= numOfPlayers.getValue(); i++) {
      var tempUser = new Player(String.valueOf(i), "User" + i);
      _players.add(tempUser);
    }
    this.players = _players;
    gameBoard = new GameBoard(numOfPlayers);
  }

  public void startGame() throws GameStartupException {
    if (gameBoard == null || players == null || players.isEmpty() ||
        !players.stream().allMatch(player ->
            player.getResources().isEmpty() &&
                player.getBuildings().isEmpty() &&
                player.getPoints() == 0)
    ) {
      throw new GameStartupException();
    }
    Collections.shuffle(players);
    players = Collections.unmodifiableList(players);
    distributeCoin();
    currentPlayer = players.get(0);
    isTraded = false;
//        REPEAT for Each user (in order)
//        currentuser = reference of the user in action
//        availabeFactories = new ArrayList(currentuser.getFactories());
//        isTraded = false
//        wait for actions = actions
//        SWITCH(actions)
//        case TRADE:
//        check isTraded == false
//        according to the target Card type , call the correct trade function
//        1. if trade FirstTier resources, call tradeFirstTierResource(payment, target);
//        2. if trade factory, call tradeFactory(payment, target);
//        isTraded = true
//        case PRODUCE:
//        check the Factory's produces Card, produce accordingly
//        this.produceCard(card)
//        availableFactories.remove(factory)
//
//        case BUILDING_EFFECT:
//          doBuildEffect()
//        case END:
//        check if the current user have enough points to win, if
//        yes: break
//                no: continue
//                END REPEAT
//        display winner
  }

  public void tradeCardByCoins(List<Card> targets) throws DisplayPileException {
    tradeCardAfterPaymentValidation(currentPlayer.getCoin(), targets);
    currentPlayer.spendCoin();
  }

  public void tradeCardByCards(List<ResourceCard> payment, List<Card> targets)
      throws DisplayPileException {
    // the trading must be 1:n or n:1
    if (payment.size() != 1 || targets.size() != 1) {
      // todo: throw exception
      System.out.println("Trading must be 1:n or n:1! Will not trade!");
    }

    if (!currentPlayer.isOwnAllResources(payment)) {
      // todo: throw exception
      System.out.println("Player do not own the payment cards! Will not trade!");
      return;
    }

    var sumOfValue = targets.stream().map(Card::getValue).reduce(0, Integer::sum);
    tradeCardAfterPaymentValidation(sumOfValue, targets);
    currentPlayer.discardCards(payment);
  }

  public void activateFactory(BuildingCard card, List<ResourceCard> effectCost,
      List<ResourceCard> effectCapital) {
    // todo: implement
  }

  private void tradeCardAfterPaymentValidation(int payment, List<Card> targets)
      throws DisplayPileException {
    if (isTraded) {
      // todo: throw exception
      System.out.println("Player already traded!");
      return;
    }

    if (payment >= targets.stream().map(Card::getValue).reduce(0, Integer::sum)) {

      gameBoard.takeCards(targets);

      var targetResourceCards = targets.stream()
          .filter(card -> card.cardType() == CardType.BASIC_RESOURCE ||
              card.cardType() == CardType.LEVEL_ONE_RESOURCE ||
              card.cardType() == CardType.LEVEL_TWO_RESOURCE)
          .map(card -> (ResourceCard) card)
          .toList();
      var targetBuildingCards = targets.stream()
          .filter(card -> card.cardType() == CardType.BUILDING)
          .map(card -> (BuildingCard) card)
          .toList();

      currentPlayer.takeResourceCards(targetResourceCards);
      currentPlayer.takeBuildingCards(targetBuildingCards);

      isTraded = true;
      System.out.println(currentPlayer.getName() + " traded " + targets + " using " + payment);
    } else {
      // todo: throw exception
      System.out.println(currentPlayer.getName() + " traded " + targets + " using " + payment
          + "is not applicable");
    }
  }

  private void distributeCoin() {
    var coins = 3;
    for (var player : players) {
      player.receiveCoin(coins++);
    }
  }

  public void endTurn() throws DisplayPileException {
    currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    gameBoard.refillCards();
    isTraded = false;
  }
}
