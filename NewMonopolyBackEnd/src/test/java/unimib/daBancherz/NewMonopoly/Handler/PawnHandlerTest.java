package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class PawnHandlerTest {

    private PawnHandler pawnHandler;

    @Mock
    private GameHandler mockGameHandler;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private BoxHandler mockBoxHandler;
    @Mock
    private GameBoardSingleton mockGameBoard;
    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pawnHandler = new PawnHandler(mockGameHandler, mockMessageService, mockBoxHandler);
        pawnHandler.gameBoard = mockGameBoard;  // Injecting the mock singleton
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

        pawnHandler.movimentoPedina(gameId, playerName, newPosition, gameSessions, pawnId, mockSession, viaPay);

        verify(mockGameBoard, times(1)).setPlayerPosition(gameId, playerName, newPosition);
        verify(mockMessageService, times(1)).sendPawnMove(pawnId, playerName, newPosition, gameSessions, gameId);
        verify(mockBoxHandler, times(1)).sendBoxUsage(playerName, mockSession, newPosition, gameId, gameSessions, pawnId, viaPay);
    }
}