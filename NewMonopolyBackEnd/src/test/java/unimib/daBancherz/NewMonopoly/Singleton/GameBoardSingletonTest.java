package unimib.daBancherz.NewMonopoly.Singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameBoardSingletonTest {
    private GameBoardSingleton gameBoard;
    private final String gameId = "testGame";
    private final String playerName = "player1";

    @BeforeEach
    void setUp() {
        gameBoard = GameBoardSingleton.getInstance();
        gameBoard.createGame(gameId);
    }

    @Test
    void testCreateGame() {
        assertNotNull(gameBoard.getGame(gameId), "Il gioco dovrebbe essere stato creato.");
    }

    @Test
    void testSetAndGetPlayerPosition() {
        gameBoard.setPlayerPosition(gameId, playerName, 5);
        assertEquals(5, gameBoard.getPlayerPosition(gameId, playerName), "La posizione del giocatore dovrebbe essere 5.");
    }

    @Test
    void testRemoveGame() {
        gameBoard.removeGame(gameId);
        assertNull(gameBoard.getGame(gameId), "Il gioco dovrebbe essere stato rimosso.");
    }

    @Test
    void testRemovePlayerFromGame() {
        gameBoard.setPlayerPosition(gameId, playerName, 5);
        gameBoard.removePlayerFromGame(gameId, playerName);
        assertNull(gameBoard.getPlayerPosition(gameId, playerName), "Il giocatore dovrebbe essere stato rimosso.");
    }

    @Test
    void testSetAndCheckPlayerInPrison() {
        gameBoard.setPlayerPrison(gameId, playerName, true);
        assertTrue(gameBoard.isPlayerInPrison(gameId, playerName), "Il giocatore dovrebbe essere in prigione.");
        gameBoard.setPlayerPrison(gameId, playerName, false);
        assertFalse(gameBoard.isPlayerInPrison(gameId, playerName), "Il giocatore non dovrebbe essere in prigione.");
    }

    @Test
    void testSetAndGetPlayerCountRoll() {
        gameBoard.setPlayerCountRoll(gameId, playerName, 2);
        assertEquals(2, gameBoard.getPlayerCountRoll(gameId, playerName), "Il numero di tiri dovrebbe essere 2.");
    }

    @Test
    void testSetAndGetPlayerCountRollDoubleDice() {
        gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 3);
        assertEquals(3, gameBoard.getPlayerCountRollDoubleDice(gameId, playerName), "Il numero di tiri doppi dovrebbe essere 3.");
    }

    @Test
    void testRemoveGameIfEmpty() {
        gameBoard.removePlayerFromGame(gameId, playerName);
        gameBoard.removeGameIfEmpty(gameId);
        assertNull(gameBoard.getGame(gameId), "Il gioco dovrebbe essere stato rimosso se vuoto.");
    }
}