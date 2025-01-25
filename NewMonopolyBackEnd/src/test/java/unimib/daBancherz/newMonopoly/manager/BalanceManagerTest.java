package unimib.daBancherz.newMonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.newMonopoly.handler.GameHandler;
import unimib.daBancherz.newMonopoly.MessageService;
import unimib.daBancherz.newMonopoly.database.repository.GiocatoreRepository;

import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

class BalanceManagerTest {

    private BalanceManager balanceManager;

    @Mock
    private GiocatoreRepository mockGiocatoreRepository;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private GameHandler mockGameHandler;
    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        balanceManager = new BalanceManager(mockGiocatoreRepository, mockMessageService, mockGameHandler);
    }

    @Test
    void testCheckBalancePositive() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = 100;

        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        // Mock game sessions to have more than one player
        when(mockGameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(mockSession, mock(WebSocketSession.class))));

        balanceManager.checkBalance(gameId, playerName, mockSession);

        verify(mockMessageService).updateProperties(gameId, playerName, mockSession);
        verify(mockMessageService, never()).sendLoseMessage(any());
        verify(mockMessageService, never()).sendVictoryMessage(any());
    }

    @Test
    void testCheckBalanceNegative() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = -100;

        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        // Mock game sessions to have more than one player
        when(mockGameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(mockSession, mock(WebSocketSession.class))));

        balanceManager.checkBalance(gameId, playerName, mockSession);

        verify(mockMessageService).sendLoseMessage(mockSession);
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha perso"), anyMap(), eq(mockSession));
        verify(mockGameHandler).removePlayerFromGame(gameId, mockSession);
        verify(mockMessageService, never()).sendVictoryMessage(any());
    }


    @Test
    void testCheckBalanceSinglePlayer() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = 100;

        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        when(mockGameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(mockSession)));

        balanceManager.checkBalance(gameId, playerName, mockSession);

        verify(mockMessageService).updateProperties(gameId, playerName, mockSession);
        verify(mockMessageService).sendVictoryMessage(mockSession);
    }

    @Test
    void testHandleNegativeBalance() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        balanceManager.handleNegativeBalance(gameId, playerName, mockSession);

        verify(mockMessageService).sendLoseMessage(mockSession);
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha perso"), anyMap(), eq(mockSession));
        verify(mockGameHandler).removePlayerFromGame(gameId, mockSession);
    }
}