package unimib.daBancherz.newMonopoly.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unimib.daBancherz.newMonopoly.model.Game;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameBoardWrapperTest {

    private GameBoardWrapper gameBoardWrapper;

    @Mock
    private GameBoardSingleton mockGameBoardSingleton;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameBoardWrapper = new GameBoardWrapper();
        gameBoardWrapper.gameBoardSingleton = mockGameBoardSingleton;  // Injecting the mock singleton
    }

    @Test
    void testCreateGame() {
        String gameId = "game123";

        gameBoardWrapper.createGame(gameId);

        verify(mockGameBoardSingleton, times(1)).createGame(gameId);
    }

    @Test
    void testGetGame() {
        String gameId = "game123";
        Game mockGame = new Game(gameId);
        when(mockGameBoardSingleton.getGame(gameId)).thenReturn(mockGame);

        Game result = gameBoardWrapper.getGame(gameId);

        assertEquals(mockGame, result);
        verify(mockGameBoardSingleton, times(1)).getGame(gameId);
    }

    @Test
    void testSetPlayerPosition() {
        String gameId = "game123";
        String playerName = "player1";
        int position = 5;

        gameBoardWrapper.setPlayerPosition(gameId, playerName, position);

        verify(mockGameBoardSingleton, times(1)).setPlayerPosition(gameId, playerName, position);
    }

    @Test
    void testGetPlayerPosition() {
        String gameId = "game123";
        String playerName = "player1";
        int expectedPosition = 5;
        when(mockGameBoardSingleton.getPlayerPosition(gameId, playerName)).thenReturn(expectedPosition);

        int result = gameBoardWrapper.getPlayerPosition(gameId, playerName);

        assertEquals(expectedPosition, result);
        verify(mockGameBoardSingleton, times(1)).getPlayerPosition(gameId, playerName);
    }

    @Test
    void testGetPlayerPositions() {
        String gameId = "game123";
        Map<String, Integer> mockPositions = Map.of("player1", 5, "player2", 10);
        when(mockGameBoardSingleton.getPlayerPositions(gameId)).thenReturn(mockPositions);

        Map<String, Integer> result = gameBoardWrapper.getPlayerPositions(gameId);

        assertEquals(mockPositions, result);
        verify(mockGameBoardSingleton, times(1)).getPlayerPositions(gameId);
    }

    @Test
    void testRemoveGame() {
        String gameId = "game123";

        gameBoardWrapper.removeGame(gameId);

        verify(mockGameBoardSingleton, times(1)).removeGame(gameId);
    }

    @Test
    void testRemovePlayerFromGame() {
        String gameId = "game123";
        String playerName = "player1";

        gameBoardWrapper.removePlayerFromGame(gameId, playerName);

        verify(mockGameBoardSingleton, times(1)).removePlayerFromGame(gameId, playerName);
    }

    @Test
    void testIsPlayerInPrison() {
        String gameId = "game123";
        String playerName = "player1";
        when(mockGameBoardSingleton.isPlayerInPrison(gameId, playerName)).thenReturn(true);

        boolean result = gameBoardWrapper.isPlayerInPrison(gameId, playerName);

        assertTrue(result);
        verify(mockGameBoardSingleton, times(1)).isPlayerInPrison(gameId, playerName);
    }

    @Test
    void testSetPlayerPrison() {
        String gameId = "game123";
        String playerName = "player1";
        boolean isInPrison = true;

        gameBoardWrapper.setPlayerPrison(gameId, playerName, isInPrison);

        verify(mockGameBoardSingleton, times(1)).setPlayerPrison(gameId, playerName, isInPrison);
    }
}