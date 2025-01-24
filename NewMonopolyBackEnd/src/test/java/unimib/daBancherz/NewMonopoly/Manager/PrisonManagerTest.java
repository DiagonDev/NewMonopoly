package unimib.daBancherz.NewMonopoly.Manager;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardWrapper;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaOpportunitaRepository;

@ExtendWith(MockitoExtension.class)
class PrisonManagerTest {

    @Mock
    private GameBoardSingleton gameBoardSingleton;

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
    private GameBoardWrapper gameBoardWrapper;

    @InjectMocks
    private PrisonManager prisonManager;

    private final String gameId = "game1";
    private final String playerName = "player1";

    @BeforeEach
    void setUp() {
        gameBoardWrapper = new GameBoardWrapper(gameBoardSingleton);
    }

    @Test
    void testIsPlayerInPrison() {
        when(gameBoardSingleton.isPlayerInPrison(gameId, playerName)).thenReturn(true);
        assertTrue(prisonManager.isPlayerInPrison(gameId, playerName));
        verify(gameBoardSingleton, times(1)).isPlayerInPrison(gameId, playerName);
    }

    @Test
    void testHandlePrisonPlayer_WithProbabilityCard() throws Exception {
        when(partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")).thenReturn(true);
        prisonManager.handlePrisonPlayer(gameId, playerName, session);
        verify(gameBoardSingleton, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(messageService, times(1)).exitPrisonMessage(true, session);
    }

    @Test
    void testLasciaPrigione_DoubleDice() throws Exception {
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameBoardSingleton.getPlayerCountRoll(gameId, playerName)).thenReturn(2);

        boolean result = prisonManager.lasciaPrigione(session, new int[]{3, 3});

        assertTrue(result);
        verify(gameBoardSingleton, times(1)).setPlayerPrison(gameId, playerName, false);
    }

    @Test
    void testPayPrisonExit_NotEnoughBalance() throws Exception {
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(30);

        prisonManager.payPrisonExit(session);

        verify(messageService, times(1)).exitPrisonMessage(false, session);
    }

    @Test
    void testPayPrisonExit_SufficientBalance() throws Exception {
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(100);

        prisonManager.payPrisonExit(session);

        verify(gameBoardSingleton, times(1)).setPlayerPrison(gameId, playerName, false);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 50);
    }
}
