package unimib.daBancherz.NewMonopoly.Singleton;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.model.Game;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardSingletonTest {
    private GameBoardSingleton gameBoard;
    private final String gameId = "game1";
    private final String playerName = "player1";

    @BeforeEach
    void setUp() {
        gameBoard = GameBoardSingleton.getInstance();
        gameBoard.createGame(gameId);

    }

    @Test
    void testSingletonInstance() {
        GameBoardSingleton instance1 = GameBoardSingleton.getInstance();
        GameBoardSingleton instance2 = GameBoardSingleton.getInstance();
        assertSame(instance1, instance2, "GameBoardSingleton should be a singleton");
    }

    @Test
    void testCreateGame() {
        assertNotNull(gameBoard.getGame(gameId), "Game should be created successfully");
    }

    @Test
    void testGetGame() {
        Game game = gameBoard.getGame(gameId);
        assertNotNull(game, "Should return the game instance");
    }

    @Test
    void testSetPlayerPosition() {
        gameBoard.setPlayerPosition(gameId, playerName, 5);
        assertEquals(5, gameBoard.getPlayerPosition(gameId, playerName));
    }

    @Test
    void testGetPlayerPositionForNonExistentPlayer() {
        assertNull(gameBoard.getPlayerPosition(gameId, "unknownPlayer"));
    }

    @Test
    void testGetPlayerPositions() {
        gameBoard.setPlayerPosition(gameId, playerName, 3);
        Map<String, Integer> positions = gameBoard.getPlayerPositions(gameId);
        assertEquals(3, positions.get(playerName));
    }

    @Test
    void testRemoveGame() {
        gameBoard.removeGame(gameId);
        assertNull(gameBoard.getGame(gameId));
    }

    @Test
    void testRemovePlayerFromGame() {
        gameBoard.setPlayerPosition(gameId, playerName, 3);
        gameBoard.removePlayerFromGame(gameId, playerName);
        assertNull(gameBoard.getPlayerPosition(gameId, playerName));
    }

    @Test
    void testRemoveGameIfEmpty() {
        gameBoard.setPlayerPosition(gameId, playerName, 5);
        gameBoard.removeGameIfEmpty(gameId);
        assertNotNull(gameBoard.getGame(gameId), "Game should still exist as it's not empty");

        gameBoard.removePlayerFromGame(gameId, playerName);  // Now game should be empty
        gameBoard.removeGameIfEmpty(gameId);
        assertNull(gameBoard.getGame(gameId), "Game should be removed if empty");
    }

    @Test
    void testSetPlayerPrison() {
        gameBoard.setPlayerPrison(gameId, playerName, true);
        assertTrue(gameBoard.isPlayerInPrison(gameId, playerName));
    }

    @Test
    void testIsPlayerInPrisonWhenNotSet() {
        assertFalse(gameBoard.isPlayerInPrison(gameId, playerName), "Player should not be in prison by default");
    }

    @Test
    void testSetPlayerCountRoll() {
        gameBoard.setPlayerCountRoll(gameId, playerName, 2);
        assertEquals(2, gameBoard.getPlayerCountRoll(gameId, playerName));
    }

    @Test
    void testGetPlayerCountRollForNonExistentPlayer() {
        assertEquals(0, gameBoard.getPlayerCountRoll(gameId, "unknownPlayer"));
    }

    @Test
    void testSetPlayerCountRollDoubleDice() {
        gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 3);
        assertEquals(3, gameBoard.getPlayerCountRollDoubleDice(gameId, playerName));
    }

    @Test
    void testGetPlayerCountRollDoubleDiceForNonExistentPlayer() {
        assertEquals(0, gameBoard.getPlayerCountRollDoubleDice(gameId, "unknownPlayer"));
    }
}