package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaRepository;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class PawnManagerTest {
    @InjectMocks
    private PawnManager pawnManager;
    @Mock
    private GameHandler gameHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private BoxManager boxManager;
    @Mock
    private GameBoardSingleton gameBoard;
    @Mock
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pawnManager = new PawnManager(gameHandler, messageService, boxManager);
        pawnManager.gameBoard = gameBoard;  // Injecting the mock singleton
    }

    @Test
    void testMovimentoPedina() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int newPosition = 5;
        int pawnId = 1;
        boolean viaPay = true;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        when(gameHandler.getGameSessions()).thenReturn(gameSessions);

        pawnManager.movimentoPedina(gameId, playerName, newPosition, pawnId, session, viaPay);

        verify(gameBoard, times(1)).setPlayerPosition(gameId, playerName, newPosition);
        verify(messageService, times(1)).sendPawnMove(pawnId, playerName, newPosition, gameSessions, gameId);
        verify(boxManager, times(1)).sendBoxUsage(playerName, session, newPosition, gameId, gameSessions, pawnId, viaPay);
    }
}