package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;

import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

class BalanceManagerTest {
    @InjectMocks
    private BalanceManager balanceManager;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private GameHandler gameHandler;
    @Mock
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        balanceManager = new BalanceManager(giocatoreRepository, messageService, gameHandler);
    }

    @Test
    void testCheckBalancePositive() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = 100;

        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        // Mock game sessions to have more than one player
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session, mock(WebSocketSession.class))));

        balanceManager.checkBalance(gameId, playerName, session);

        verify(messageService).updateProperties(gameId, playerName, session);
        verify(messageService, never()).sendLoseMessage(any());
        verify(messageService, never()).sendVictoryMessage(any());
    }

    @Test
    void testCheckBalanceSinglePlayer() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = 100;

        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session)));

        balanceManager.checkBalance(gameId, playerName, session);

        verify(messageService).updateProperties(gameId, playerName, session);
        verify(messageService).sendVictoryMessage(session);
    }

    @Test
    void testCheckBalanceNegative() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        int saldoG = -10;

        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(saldoG);
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session, mock(WebSocketSession.class))));
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);

        balanceManager.checkBalance(gameId, playerName, session);

        verify(messageService).sendLoseMessage(session);
        verify(messageService).sendSystemMessage(eq(gameId), contains("ha perso"), any(), eq(session));
        verify(gameHandler).removePlayerFromGame(gameId, session);
    }

    @Test
    void testCheckWinWithMultiplePlayers() throws Exception {
        String gameId = "game123";
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session, mock(WebSocketSession.class))));

        balanceManager.checkWin(gameId);

        verify(messageService, never()).sendVictoryMessage(any());
    }

    @Test
    void testCheckWinWithSinglePlayer() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session)));
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);

        balanceManager.checkWin(gameId);

        verify(messageService).sendSystemMessage(eq(gameId), contains("ha vinto"), any(), eq(session));
        verify(messageService).sendVictoryMessage(session);
        verify(gameHandler).removePlayerFromGame(gameId, session);
    }

}