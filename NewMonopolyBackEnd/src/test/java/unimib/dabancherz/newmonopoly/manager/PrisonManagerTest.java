package unimib.dabancherz.newmonopoly.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaOpportunitaRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrisonManagerTest {

    @Mock
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private GameHandler gameHandler;
    @Mock
    private WebSocketSession session;
    @Mock
    private GameBoardSingleton gameBoard;

    @InjectMocks
    private PrisonManager prisonManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prisonManager = new PrisonManager(partitaOpportunitaRepository, giocatoreRepository, messageService, gameHandler);
        prisonManager.gameBoard = gameBoard; // Inject the mock GameBoardSingleton
    }

    @Test
    void isPlayerInPrison() {
        String gameId = "game-1";
        String playerName = "player1";
        when(gameBoard.isPlayerInPrison(gameId, playerName)).thenReturn(true);
        boolean result = prisonManager.isPlayerInPrison(gameId, playerName);
        assertTrue(result);
        verify(gameBoard, times(1)).isPlayerInPrison(gameId, playerName);
    }

    @Test
    void handlePrisonPlayer_WithProbabilityCard() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(true);
        prisonManager.handlePrisonPlayer(gameId, playerName, session);
        verify(partitaOpportunitaRepository, times(1)).possiedeCarta(playerName, gameId, "Probabilità");
        verify(partitaOpportunitaRepository, times(1)).setGiocatore(gameId, null, "esci_prigione", "Probabilità");
        verify(gameBoard, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(gameBoard, times(1)).setPlayerCountRoll(gameId, playerName, 0);
        verify(messageService, times(1)).exitPrisonMessage(true, session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), eq(playerName + " è uscito di prigione"), any(), eq(session));
    }

    @Test
    void handlePrisonPlayer_WithChanceCard() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(false);
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Imprevisto")).thenReturn(true);
        prisonManager.handlePrisonPlayer(gameId, playerName, session);
        verify(partitaOpportunitaRepository, times(1)).possiedeCarta(playerName, gameId, "Probabilità");
        verify(partitaOpportunitaRepository, times(1)).possiedeCarta(playerName, gameId, "Imprevisto");
        verify(partitaOpportunitaRepository, times(1)).setGiocatore(gameId, null, "esci_prigione", "Imprevisto");
        verify(gameBoard, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(gameBoard, times(1)).setPlayerCountRoll(gameId, playerName, 0);
        verify(messageService, times(1)).exitPrisonMessage(true, session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), eq(playerName + " è uscito di prigione"), any(), eq(session));
    }

    @Test
    void handlePrisonPlayer_WithoutCard() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(false);
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Imprevisto")).thenReturn(false);
        prisonManager.handlePrisonPlayer(gameId, playerName, session);
        verify(partitaOpportunitaRepository, times(1)).possiedeCarta(playerName, gameId, "Probabilità");
        verify(partitaOpportunitaRepository, times(1)).possiedeCarta(playerName, gameId, "Imprevisto");
    }

    @Test
    void sendPrisonMessage() throws Exception {
        prisonManager.sendPrisonMessage(session);
        String prisonMessage = new ObjectMapper().writeValueAsString(Map.of("type", "prison"));
        verify(session, times(1)).sendMessage(new TextMessage(prisonMessage));
    }

    @Test
    void lasciaPrigione_WithDoubleDice() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        int[] diceResults = {3, 3};
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        boolean result = prisonManager.lasciaPrigione(session, diceResults);
        assertTrue(result);
        verify(gameBoard, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(gameBoard, times(1)).setPlayerCountRoll(gameId, playerName, 0);
        verify(messageService, times(1)).exitPrisonMessage(true, session);
    }

    @Test
    void lasciaPrigione_WithoutDoubleDice() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        int[] diceResults = {1, 2};
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameBoard.getPlayerCountRoll(gameId, playerName)).thenReturn(2);
        boolean result = prisonManager.lasciaPrigione(session, diceResults);
        assertFalse(result);
        verify(gameBoard, times(1)).setPlayerCountRoll(gameId, playerName, 3);
        verify(messageService, never()).exitPrisonMessage(anyBoolean(), eq(session));
    }

    @Test
    void payPrisonExit_WithInsufficientBalance() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(30);
        prisonManager.payPrisonExit(session);
        verify(messageService, times(1)).exitPrisonMessage(false, session);
    }

    @Test
    void payPrisonExit_WithSufficientBalance() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(100);
        prisonManager.payPrisonExit(session);
        verify(gameBoard, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 50);
        verify(messageService, times(1)).updateBalance(any(), eq(gameId), eq(playerName));
        verify(gameBoard, times(1)).setPlayerCountRoll(gameId, playerName, 0);
        verify(messageService, times(1)).exitPrisonMessage(true, session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), eq(playerName + " è uscito di prigione"), any(), eq(session));
    }
}