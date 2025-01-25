package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaOpportunitaRepository;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PrisonManagerTest {

    private PrisonManager prisonManager;

    @Mock
    private PartitaOpportunitaRepository mockPartitaOpportunitaRepository;
    @Mock
    private GiocatoreRepository mockGiocatoreRepository;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private GameHandler mockGameHandler;
    @Mock
    private GameBoardSingleton mockGameBoard;
    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prisonManager = new PrisonManager(mockPartitaOpportunitaRepository, mockGiocatoreRepository, mockMessageService, mockGameHandler);
        prisonManager.gameBoard = mockGameBoard;  // Injecting the mock game board
    }

    @Test
    void testIsPlayerInPrison() {
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameBoard.isPlayerInPrison(gameId, playerName)).thenReturn(true);

        boolean result = prisonManager.isPlayerInPrison(gameId, playerName);

        assertTrue(result);
        verify(mockGameBoard, times(1)).isPlayerInPrison(gameId, playerName);
    }

    @Test
    void testHandlePrisonPlayerWithProbabilityCard() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockPartitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(true);

        prisonManager.handlePrisonPlayer(gameId, playerName, mockSession);

        verify(mockPartitaOpportunitaRepository).setGiocatore(gameId, null, "esci_prigione", "Probabilità");
        verify(mockMessageService).exitPrisonMessage(true, mockSession);
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("è uscito di prigione"), anyMap(), eq(mockSession));
    }

    @Test
    void testHandlePrisonPlayerWithoutCards() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockPartitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(false);
        when(mockPartitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Imprevisto")).thenReturn(false);

        prisonManager.handlePrisonPlayer(gameId, playerName, mockSession);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(mockSession).sendMessage(captor.capture());
        TextMessage textMessage = captor.getValue();
        assertTrue(textMessage.getPayload().contains("\"type\":\"prison\""));
    }

    @Test
    void testLasciaPrigioneSuccess() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int[] diceResults = {3, 3};

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        boolean result = prisonManager.lasciaPrigione(mockSession, diceResults);

        assertTrue(result);
        verify(mockGameBoard).setPlayerPrison(gameId, playerName, false);
        verify(mockGameBoard).setPlayerCountRoll(gameId, playerName, 0);
        verify(mockMessageService).exitPrisonMessage(true, mockSession);
    }

    @Test
    void testLasciaPrigioneFailure() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int[] diceResults = {3, 4};

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        boolean result = prisonManager.lasciaPrigione(mockSession, diceResults);

        assertFalse(result);
        verify(mockGameBoard, never()).setPlayerPrison(gameId, playerName, false);
        verify(mockMessageService, never()).exitPrisonMessage(true, mockSession);
    }

    @Test
    void testPayPrisonExitInsufficientBalance() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(30);

        prisonManager.payPrisonExit(mockSession);

        verify(mockMessageService).exitPrisonMessage(false, mockSession);
        verify(mockGameBoard, never()).setPlayerPrison(gameId, playerName, false);
    }

    @Test
    void testPayPrisonExitSufficientBalance() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(100);

        prisonManager.payPrisonExit(mockSession);

        verify(mockGameBoard).setPlayerPrison(gameId, playerName, false);
        verify(mockGiocatoreRepository).setSaldoGiocatore(playerName, gameId, 50);
        verify(mockMessageService).updateBalance(anyMap(), eq(gameId), eq(playerName));
        verify(mockMessageService).exitPrisonMessage(false, mockSession);
    }
}