package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class PawnManagerTest {

    private PawnManager pawnManager;

    @Mock
    private GameHandler mockGameHandler;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private BoxManager mockBoxManager;
    @Mock
    private GameBoardSingleton mockGameBoard;
    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pawnManager = new PawnManager(mockGameHandler, mockMessageService, mockBoxManager);
        pawnManager.gameBoard = mockGameBoard;  // Injecting the mock singleton
    }

    @Test
    void testMovimentoPedina() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int newPosition = 5;
        int pawnId = 1;
        boolean viaPay = true;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(mockSession));

        when(mockGameHandler.getGameSessions()).thenReturn(gameSessions);

        pawnManager.movimentoPedina(gameId, playerName, newPosition, pawnId, mockSession, viaPay);

        verify(mockGameBoard, times(1)).setPlayerPosition(gameId, playerName, newPosition);
        verify(mockMessageService, times(1)).sendPawnMove(pawnId, playerName, newPosition, gameSessions, gameId);
        verify(mockBoxManager, times(1)).sendBoxUsage(playerName, mockSession, newPosition, gameId, gameSessions, pawnId, viaPay);
    }
}