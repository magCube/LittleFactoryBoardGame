package org.magcube

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.magcube.card.BuildingCard
import org.magcube.card.Card
import org.magcube.card.CardType
import org.magcube.card.ResourceCard
import org.magcube.exception.DisplayPileException
import org.magcube.exception.NumOfPlayersException

class GameBoardTest {
    @ParameterizedTest
    @ValueSource(ints = [2, 3, 4])
    fun constructorTest(numOfPlayers: Int) {
        assertDoesNotThrow<GameBoard> { GameBoard(numOfPlayers) }
    }

    @ParameterizedTest
    @ValueSource(ints = [-5, -1, 0, 1, 5])
    fun constructorShouldThrowTest(numOfPlayers: Int) {
        assertThrows(NumOfPlayersException::class.java) { GameBoard(numOfPlayers) }
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun takeCardsTest() {
        val gameBoard = GameBoard(4)
        // todo: this test should change to take clone cards
        val firstCard = gameBoard.displayingBasicResource[0][0]
        assertDoesNotThrow { gameBoard.takeCards(listOf<Card>(firstCard)) }
        assertNotSame(firstCard, gameBoard.displayingBasicResource[0][0])
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun takeCardsTest2() {
        val gameBoard = GameBoard(4)
        val firstCard = gameBoard.displayingBasicResource[0][0]
        val secondCard = gameBoard.displayingBasicResource[1][0]
        val buildingCard = gameBoard.displayingBuildings[0][0]
        assertDoesNotThrow { gameBoard.takeCards(listOf(firstCard, secondCard, buildingCard)) }
        assertNotSame(firstCard, gameBoard.displayingBasicResource[0][0])
        assertNotSame(secondCard, gameBoard.displayingBasicResource[1][0])
        assertNotSame(buildingCard, gameBoard.displayingBuildings[0][0])
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun takeCardsFailTest() {
        val gameBoard = GameBoard(4)
        val firstCard = ResourceCard.builder().build()
        val secondCard = ResourceCard.builder().build()
        val buildingCard = BuildingCard.builder().build()
        assertThrows(
            DisplayPileException::class.java
        ) { gameBoard.takeCards(listOf(firstCard, secondCard, buildingCard)) }
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun discardCardsTest() {
        val gameBoard = GameBoard(4)
        val card = ResourceCard.builder()
                .cardType(CardType.BASIC_RESOURCE)
                .name("test1")
                .build()
        assertDoesNotThrow { gameBoard.discardCards(listOf<Card>(card)) }
        assertEquals(1,
                gameBoard.basicResourcesPile.discardPileSize())
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun giveCardsTest2() {
        val gameBoard = GameBoard(4)
        val card = BuildingCard.builder()
                .cardType(CardType.BUILDING)
                .name("test1")
                .build()
        assertDoesNotThrow { gameBoard.discardCards(listOf<Card>(card)) }
        assertEquals(1,
                gameBoard.buildingPile.discardPileSize())
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun refillGameBoardTest() {
        val gameBoard = GameBoard(4)
        assertNotNull(gameBoard.displayingBasicResource[0][0])
        assertNotNull(gameBoard.displayingBasicResource[1][0])
        assertNotNull(gameBoard.displayingBuildings[0][0])
        gameBoard.refillCards()
        assertEquals(5, gameBoard.displayingBuildings.size)
        assertTrue(gameBoard.displayingBuildings.stream()
                .allMatch { cards: ArrayList<BuildingCard?>? -> !cards.isNullOrEmpty() })
        assertEquals(5, gameBoard.displayingBasicResource.size)
        assertTrue(gameBoard.displayingBasicResource.stream().allMatch { cards: ArrayList<ResourceCard?>? -> !cards.isNullOrEmpty() })
    }
}