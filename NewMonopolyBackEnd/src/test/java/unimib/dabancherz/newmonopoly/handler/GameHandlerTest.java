package unimib.dabancherz.newmonopoly.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.*;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.database.service.GameService;
import unimib.dabancherz.newmonopoly.singleton.GameBoardWrapper;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GameHandlerTest {
    @Mock
    private MessageService messageService;
    @Mock
    private GameService gameService;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private PartitaRepository partitaRepository;
    @Mock
    private PedinaRepository pedinaRepository;
    @Mock
    private CasellaRepository casellaRepository;
    @Mock
    private GameBoardWrapper gameBoardWrapper;
    @Mock
    private WebSocketSession session;
    @InjectMocks
    private GameHandler gameHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameHandler = new GameHandler(messageService, gameService, giocatoreRepository, partitaRepository, pedinaRepository, casellaRepository, gameBoardWrapper);
    }
    @Test
    void createGame() throws Exception {
        String[] messageParts = {"create", "player1", "easy", "random"};
        when(casellaRepository.findByOrder(anyString())).thenReturn(new int[]{1, 2, 3});
        when(pedinaRepository.findUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());
        when(partitaRepository.findLastCodiceInvito()).thenReturn(null);
        gameHandler.createGame(messageParts, session);
        verify(messageService, times(1)).sendBoxOrderMessage(any(), any(), anyString());
        verify(messageService, times(1)).sendGameId(anyString(), any());
        verify(messageService, times(1)).sendSystemMessage(anyString(), anyString(), any(), any());
        verify(messageService, times(1)).notifyPlayerJoin(anyString(), anyString(), any(), any(), eq("ADMIN"));
        verify(messageService, times(1)).sendTypePlayer(anyString(), any());
        verify(messageService, times(1)).sendUnusedPedine(any(), any(), anyString());
        verify(gameBoardWrapper, times(1)).createGame(anyString());
        verify(gameBoardWrapper, times(1)).setPlayerPosition(anyString(), anyString(), eq(1));
        verify(gameService, times(1)).createGameAndPlayer(anyString(), anyString(), anyString(), anyString());
    }
    @Test
    void joinGame() throws Exception {
        String[] messageParts = {"join", "player1", "game-1"};
        String gameId = "game-1";
        String playerName = "player1";
        when(casellaRepository.findByOrder(gameId)).thenReturn(new int[]{1, 2, 3});
        when(giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito(playerName, gameId)).thenReturn(false);
        when(gameService.getUnusedPedineByPartita(gameId)).thenReturn(new ArrayList<>());
        doNothing().when(messageService).sendErrorMessage(any());
        doNothing().when(messageService).sendErrorGameIdMessage(any(), anyString());
        Map<String, List<WebSocketSession>> gameSessions = new HashMap<>();
        gameSessions.put(gameId, new ArrayList<>());
        var field = GameHandler.class.getDeclaredField("gameSessions");
        field.setAccessible(true);
        field.set(gameHandler, gameSessions);
        Map<WebSocketSession, String> playerNameList = new HashMap<>();
        var playerNameField = GameHandler.class.getDeclaredField("playerNameList");
        playerNameField.setAccessible(true);
        playerNameField.set(gameHandler, playerNameList);
        gameHandler.joinGame(messageParts, session);
        verify(gameService, times(1)).addPlayer(playerName, gameId);
        verify(messageService, times(1)).notifyPlayerJoin(eq(gameId), eq(playerName), eq(session), any(), eq("giocatore"));
        verify(messageService, times(1)).sendBoxOrderMessage(any(), any(), eq(gameId));
        verify(messageService, times(1)).sendUnusedPedine(any(), any(), eq(gameId));
    }
    @Test
    void getGameIdBySession() {
        String gameId = "game-1";
        List<WebSocketSession> sessions = Collections.singletonList(session);
        gameHandler.getGameSessions().put(gameId, sessions);
        String result = gameHandler.getGameIdBySession(session);
        assertEquals(gameId, result);
    }
    @Test
    void getPlayerNameBySession() throws NoSuchFieldException, IllegalAccessException {
        String playerName = "player1";
        var field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<WebSocketSession, String> playerNameList = (Map<WebSocketSession, String>) field.get(gameHandler);
        playerNameList.put(session, playerName);
        String result = gameHandler.getPlayerNameBySession(session);
        assertEquals(playerName, result);
    }
    @Test
    void getSessionByPlayerName() throws NoSuchFieldException, IllegalAccessException {
        String playerName = "player1";
        String gameId = "game-1";
        var field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<WebSocketSession, String> playerNameList = (Map<WebSocketSession, String>) field.get(gameHandler);
        playerNameList.put(session, playerName);
        gameHandler.getGameSessions().put(gameId, Collections.singletonList(session));
        WebSocketSession result = gameHandler.getSessionByPlayerName(playerName, gameId);
        assertEquals(session, result);
    }
    @Test
    void generateGameId() {
        when(partitaRepository.findLastCodiceInvito()).thenReturn("game-0");
        String result = gameHandler.generateGameId();
        assertEquals("game-1", result);
    }
    @Test
    void choosePedina() throws Exception {
        String[] messageParts = {"choosePedina", "1"};
        String gameId = "game-1";
        String playerName = "player1";
        var field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<WebSocketSession, String> playerNameList = (Map<WebSocketSession, String>) field.get(gameHandler);
        playerNameList.put(session, playerName);
        gameHandler.getGameSessions().put(gameId, Collections.singletonList(session));
        when(gameService.getUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());
        gameHandler.choosePedina(messageParts, session);
        verify(giocatoreRepository, times(1)).updatePedinaForGiocatore(eq(playerName), eq(1), eq(gameId));
        verify(messageService, times(1)).sendUnusedPedine(any(), any(), anyString());
        verify(messageService, times(1)).sendPawnMove(eq(1), eq(playerName), eq(1), any(), eq(gameId));
    }
    @Test
    void sendPositionPawn() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        String gameId = "game-1";
        String playerName = "player1";
        String anotherPlayerName = "player2";
        Giocatore giocatore = new Giocatore();
        giocatore.setNome(playerName);
        Pedina pedina = new Pedina();
        pedina.setIdPedina(1);
        giocatore.setIdpedina(pedina);
        List<Giocatore> giocatori = Collections.singletonList(giocatore);
        when(giocatoreRepository.findGiocatoreWithPedina(gameId)).thenReturn(giocatori);
        when(gameBoardWrapper.getPlayerPosition(gameId, playerName)).thenReturn(5);
        var field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<WebSocketSession, String> playerNameList = (Map<WebSocketSession, String>) field.get(gameHandler);
        playerNameList.put(session, anotherPlayerName);
        gameHandler.sendPositionPawn(gameId, session);
        verify(messageService, times(1)).sendPlayerPawnPosition(eq(1), eq(playerName), eq(5), eq(session));
    }
}