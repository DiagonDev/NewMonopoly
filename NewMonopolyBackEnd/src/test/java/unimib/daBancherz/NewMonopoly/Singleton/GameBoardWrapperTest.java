package unimib.daBancherz.NewMonopoly.Singleton;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardWrapper;
import unimib.daBancherz.NewMonopoly.model.Game;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class GameBoardWrapperTest {

    @Mock
    private GameBoardSingleton gameBoardSingleton;

    @InjectMocks
    private GameBoardWrapper gameBoardWrapper;

    private final String gameId = "game1";
    private final String playerName = "player1";

    @BeforeEach
    void setUp() {
        gameBoardWrapper = new GameBoardWrapper(gameBoardSingleton);
    }

    @Test
    void testCreateGame() {
        gameBoardWrapper.createGame(gameId);
        verify(gameBoardSingleton, times(1)).createGame(gameId);
    }

    @Test
    void testGetGame() {
        Game game = new Game("game-1");
        when(gameBoardSingleton.getGame(gameId)).thenReturn(game);
        assertEquals(game, gameBoardWrapper.getGame(gameId));
    }

    @Test
    void testSetPlayerPosition() {
        gameBoardWrapper.setPlayerPosition(gameId, playerName, 5);
        verify(gameBoardSingleton, times(1)).setPlayerPosition(gameId, playerName, 5);
    }

    @Test
    void testGetPlayerPosition() {
        when(gameBoardSingleton.getPlayerPosition(gameId, playerName)).thenReturn(5);
        assertEquals(5, gameBoardWrapper.getPlayerPosition(gameId, playerName));
    }

    @Test
    void testGetPlayerPositions() {
        Map<String, Integer> positions = Map.of(playerName, 5);
        when(gameBoardSingleton.getPlayerPositions(gameId)).thenReturn(positions);
        assertEquals(positions, gameBoardWrapper.getPlayerPositions(gameId));
    }

    @Test
    void testRemoveGame() {
        gameBoardWrapper.removeGame(gameId);
        verify(gameBoardSingleton, times(1)).removeGame(gameId);
    }

    @Test
    void testRemovePlayerFromGame() {
        gameBoardWrapper.removePlayerFromGame(gameId, playerName);
        verify(gameBoardSingleton, times(1)).removePlayerFromGame(gameId, playerName);
    }

    @Test
    void testIsPlayerInPrison() {
        when(gameBoardSingleton.isPlayerInPrison(gameId, playerName)).thenReturn(true);
        assertTrue(gameBoardWrapper.isPlayerInPrison(gameId, playerName));
    }

    @Test
    void testSetPlayerPrison() {
        gameBoardWrapper.setPlayerPrison(gameId, playerName, true);
        verify(gameBoardSingleton, times(1)).setPlayerPrison(gameId, playerName, true);
    }
}

