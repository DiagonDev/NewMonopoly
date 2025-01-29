package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.Partita;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaRepository;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerTest {
    @InjectMocks
    private TurnManager turnManager;
    @Mock
    private GameHandler gameHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private PrisonManager prisonManager;
    @Mock
    private BalanceManager balanceManager;
    @Mock
    private PartitaRepository partitaRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private PawnManager pawnManager;
    @Mock
    private WebSocketSession session;
    @Mock
    private GameBoardSingleton gameBoard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        turnManager = spy(new TurnManager(gameHandler, messageService, prisonManager, balanceManager, partitaRepository, giocatoreRepository, pawnManager));
        turnManager.gameBoard = gameBoard;
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
        TurnManager turnManagerWithMockedRandom = new TurnManager(gameHandler, messageService, prisonManager, balanceManager, partitaRepository, giocatoreRepository, pawnManager) {
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

    @Test
    void testStartTurn() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        Partita partita = new Partita();
        partita.setCodiceInvito(gameId);
        partita.setStato("Non Iniziata");
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(partitaRepository.findByCodiceInvito(gameId)).thenReturn(partita);
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session)));
        when(messageService.createTurnMessage(true, playerName)).thenReturn("Your turn message");
        when(messageService.createTurnMessage(false, playerName)).thenReturn("Not your turn message");
        when(prisonManager.isPlayerInPrison(gameId, playerName)).thenReturn(true); // Assicurati che ritorni true
        turnManager.startTurn(session);
        assertEquals("Iniziata", partita.getStato());
        verify(partitaRepository, times(1)).save(partita);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), contains("È il turno di: " + playerName), any(), eq(session));
        verify(turnManager, times(1)).notifyPlayersTurn(gameId, playerName, session);
        verify(prisonManager, times(1)).handlePrisonPlayer(gameId, playerName, session);
    }

    @Test
    void testSpostaPedina() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        int pawnId = 1;
        int playerPosition = 5;
        int[] diceResults = {3, 4};
        int newPosition = playerPosition + diceResults[0] + diceResults[1];
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session)));
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameBoard.isPlayerInPrison(gameId, playerName)).thenReturn(false);
        when(gameBoard.getPlayerCountRollDoubleDice(gameId, playerName)).thenReturn(0);
        when(giocatoreRepository.findPedinaFromGiocatore(playerName, gameId)).thenReturn(pawnId);
        when(gameBoard.getPlayerPosition(gameId, playerName)).thenReturn(playerPosition);
        doReturn(diceResults).when(turnManager).rollDice(session);
        turnManager.spostaPedina(session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), contains("ha tirato i dati: dado1 3, dado2 4"), any(), eq(session));
        verify(pawnManager, times(1)).movimentoPedina(gameId, playerName, newPosition, pawnId, session, false);
    }

    @Test
    void testSpostaPedina_WithNewPositionOver40() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        int pawnId = 1;
        int playerPosition = 38;
        int[] diceResults = {3, 4};
        int newPosition = (playerPosition + diceResults[0] + diceResults[1]) - 40;
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, List.of(session)));
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameBoard.isPlayerInPrison(gameId, playerName)).thenReturn(false);
        when(gameBoard.getPlayerCountRollDoubleDice(gameId, playerName)).thenReturn(0);
        when(giocatoreRepository.findPedinaFromGiocatore(playerName, gameId)).thenReturn(pawnId);
        when(gameBoard.getPlayerPosition(gameId, playerName)).thenReturn(playerPosition);
        doReturn(diceResults).when(turnManager).rollDice(session);
        turnManager.spostaPedina(session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), contains("ha tirato i dati: dado1 3, dado2 4"), any(), eq(session));
        verify(pawnManager, times(1)).movimentoPedina(gameId, playerName, newPosition, pawnId, session, true);
    }
}