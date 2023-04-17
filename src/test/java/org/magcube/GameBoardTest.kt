package org.magcube

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.magcube.card.*
import org.magcube.exception.DisplayPileException
import org.magcube.exception.NumOfPlayersException
import org.magcube.player.NumOfPlayers
import java.util.stream.Stream

class GameBoardTest {
    @ParameterizedTest
    @MethodSource
    fun constructorTest(numOfPlayers: NumOfPlayers) {
        assertDoesNotThrow<GameBoard> { GameBoard(numOfPlayers) }
    }

    companion object {
        @JvmStatic
        private fun constructorTest(): Stream<Arguments>? {
            return Stream.of(
                Arguments.of(NumOfPlayers.TWO),
                Arguments.of(NumOfPlayers.THREE),
                Arguments.of(NumOfPlayers.FOUR),
            )
        }
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun takeCardsTest() {
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
        // todo: this test should change to take clone cards
        val firstCard = gameBoard.displayingBasicResource[0][0]
        assertDoesNotThrow { gameBoard.takeCards(listOf<Card>(firstCard)) }
        assertNotSame(firstCard, gameBoard.displayingBasicResource[0][0])
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun takeCardsTest2() {
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
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
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
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
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
        val card = ResourceCard.builder()
            .cardIdentity(CardIdentity(CardType.BASIC_RESOURCE, 1))
                .name("test1")
                .build()
        assertDoesNotThrow { gameBoard.discardCards(listOf<Card>(card)) }
        assertEquals(1,
                gameBoard.basicResourcesPile.discardPileSize())
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun giveCardsTest2() {
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
        val card = BuildingCard.builder()
                .cardIdentity(CardIdentity(CardType.BUILDING, 1))
                .name("test1")
                .build()
        assertDoesNotThrow { gameBoard.discardCards(listOf<Card>(card)) }
        assertEquals(1,
                gameBoard.buildingPile.discardPileSize())
    }

    @Test
    @kotlin.Throws(DisplayPileException::class, NumOfPlayersException::class)
    fun refillGameBoardTest() {
        val gameBoard = GameBoard(NumOfPlayers.FOUR)
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