package unimib.daBancherz.NewMonopoly.Manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.PawnHandler;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.database.Entity.Partita;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerTest {

    private TurnManager turnManager;
    private GameHandler gameHandler;
    private MessageService messageService;
    private PrisonManager prisonManager;
    private BalanceManager balanceManager;
    private PartitaRepository partitaRepository;
    private GiocatoreRepository giocatoreRepository;
    private PawnHandler pawnHandler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        gameHandler = mock(GameHandler.class);
        messageService = mock(MessageService.class);
        prisonManager = mock(PrisonManager.class);
        balanceManager = mock(BalanceManager.class);
        partitaRepository = mock(PartitaRepository.class);
        giocatoreRepository = mock(GiocatoreRepository.class);
        pawnHandler = mock(PawnHandler.class);
        turnManager = spy(new TurnManager(gameHandler, messageService, prisonManager, balanceManager, partitaRepository, giocatoreRepository, pawnHandler));
        session = mock(WebSocketSession.class);
    }

    @Test
    void testEndTurn() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        WebSocketSession nextSession = mock(WebSocketSession.class);
        List<WebSocketSession> playersInGame = List.of(session, nextSession);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, playersInGame);
        Partita partita = mock(Partita.class);

        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameHandler.getGameSessions()).thenReturn(gameSessions);
        when(partitaRepository.findByCodiceInvito(gameId)).thenReturn(partita);

        doNothing().when(turnManager).startTurn(any(WebSocketSession.class));

        turnManager.endTurn(session);

        verify(balanceManager, times(1)).checkBalance(gameId, playerName, session);
        verify(turnManager, times(1)).startTurn(nextSession);
    }


    @Test
    void testNotifyPlayersTurn() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        WebSocketSession otherSession = mock(WebSocketSession.class);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session, otherSession));

        when(gameHandler.getGameSessions()).thenReturn(gameSessions);
        when(messageService.createTurnMessage(true, playerName)).thenReturn("Your turn message");
        when(messageService.createTurnMessage(false, playerName)).thenReturn("Not your turn message");

        turnManager.notifyPlayersTurn(gameId, playerName, session);

        verify(session, times(1)).sendMessage(new TextMessage("Your turn message"));
        verify(otherSession, times(1)).sendMessage(new TextMessage("Not your turn message"));
    }

    @Test
    void testLasciaPrigione() throws Exception {
        int[] diceResults = {1, 2};

        when(prisonManager.lasciaPrigione(session, diceResults)).thenReturn(true);

        boolean result = turnManager.lasciaPrigione(session, diceResults);

        assertTrue(result);
        verify(prisonManager, times(1)).lasciaPrigione(session, diceResults);
    }

    @Test
    void testPayPrisonExit() throws Exception {
        turnManager.payPrisonExit(session);

        verify(prisonManager, times(1)).payPrisonExit(session);
    }

    @Test
    void testRollDice() throws Exception {
        int diceR1 = 3;
        int diceR2 = 4;
        SecureRandom secureRandom = mock(SecureRandom.class);
        when(secureRandom.nextInt(6)).thenReturn(diceR1 - 1, diceR2 - 1);

        TurnManager turnManagerWithMockedRandom = new TurnManager(gameHandler, messageService, prisonManager, balanceManager, partitaRepository, giocatoreRepository, pawnHandler) {
            @Override
            public int[] rollDice(WebSocketSession session) throws Exception {
                int diceR1 = secureRandom.nextInt(6) + 1;
                int diceR2 = secureRandom.nextInt(6) + 1;
                messageService.sendDiceResults(session, diceR1, diceR2);
                return new int[]{diceR1, diceR2};
            }
        };

        int[] diceResults = turnManagerWithMockedRandom.rollDice(session);

        assertArrayEquals(new int[]{diceR1, diceR2}, diceResults);
        verify(messageService, times(1)).sendDiceResults(session, diceR1, diceR2);
    }
}