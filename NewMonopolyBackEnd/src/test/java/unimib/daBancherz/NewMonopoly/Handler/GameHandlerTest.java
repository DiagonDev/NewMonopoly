package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardWrapper;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PedinaRepository;
import unimib.daBancherz.NewMonopoly.database.Service.GameService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameHandlerTest {

    private GameHandler gameHandler;
    private MessageService messageService;
    private GameService gameService;
    private GiocatoreRepository giocatoreRepository;
    private PartitaRepository partitaRepository;
    private PedinaRepository pedinaRepository;
    private GameBoardWrapper gameBoardWrapper;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        messageService = mock(MessageService.class);
        gameService = mock(GameService.class);
        giocatoreRepository = mock(GiocatoreRepository.class);
        partitaRepository = mock(PartitaRepository.class);
        pedinaRepository = mock(PedinaRepository.class);
        gameBoardWrapper = mock(GameBoardWrapper.class);
        session = mock(WebSocketSession.class);

        gameHandler = new GameHandler(messageService, gameService, giocatoreRepository, partitaRepository, pedinaRepository, gameBoardWrapper);
    }

    @Test
    void testCreateGame() throws Exception {
        String[] messageParts = {"create", "player1", "easy", "random"};
        String gameId = "game-1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);

        when(partitaRepository.findLastCodiceInvito()).thenReturn(null);
        when(pedinaRepository.findUnusedPedineByPartita(gameId)).thenReturn(new ArrayList<>());

        gameHandler.createGame(messageParts, session);

        verify(messageService, times(1)).sendGameId(eq(gameId), eq(session));
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), eq("#" + gameId), any(), eq(session));
        verify(messageService, times(1)).notifyPlayerJoin(eq(gameId), eq("player1"), eq(session), any(), eq("ADMIN"));
        verify(messageService, times(1)).sendTypePlayer(eq("ADMIN"), eq(session));
        verify(messageService, times(1)).sendUnusedPedine(anyList(), any(), eq(gameId));

        assertEquals(playersInGame, gameHandler.getGameSessions().get(gameId));
    }

    @Test
    void testJoinGame() throws Exception {
        String[] messageParts = {"join", "player2", "game-1"};
        String gameId = "game-1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);

        when(giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito("player2", gameId)).thenReturn(false);
        when(giocatoreRepository.countGiocatoriByPartita(gameId)).thenReturn(5);
        when(pedinaRepository.findUnusedPedineByPartita(gameId)).thenReturn(new ArrayList<>());
        gameHandler.getGameSessions().put(gameId, playersInGame);

        gameHandler.joinGame(messageParts, session);

        verify(gameService, times(1)).addPlayer(eq("player2"), eq(gameId));
        verify(messageService, times(1)).notifyPlayerJoin(eq(gameId), eq("player2"), eq(session), any(), eq("giocatore"));
        verify(messageService, times(1)).sendUnusedPedine(anyList(), any(), eq(gameId));

        assertTrue(gameHandler.getGameSessions().get(gameId).contains(session));
    }

    @Test
    void testGetGameIdBySession() {
        String gameId = "game-1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);
        gameHandler.getGameSessions().put(gameId, playersInGame);

        String result = gameHandler.getGameIdBySession(session);

        assertEquals(gameId, result);
    }

    @Test
    void testGetPlayerNameBySession() throws Exception {
        String playerName = "player1";
        // Use reflection to access the private playerNameList field
        Field field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        Map<String, WebSocketSession> playerNameList = (Map<String, WebSocketSession>) field.get(gameHandler);
        playerNameList.put(playerName, session);

        String result = gameHandler.getPlayerNameBySession(session);

        assertEquals(playerName, result);
    }

    @Test
    void testGetSessionByPlayerName() throws Exception {
        String playerName = "player1";
        String gameId = "game-1";
        // Use reflection to access the private playerNameList field
        Field field = GameHandler.class.getDeclaredField("playerNameList");
        field.setAccessible(true);
        Map<String, WebSocketSession> playerNameList = (Map<String, WebSocketSession>) field.get(gameHandler);
        playerNameList.put(playerName, session);
        gameHandler.getGameSessions().put(gameId, List.of(session));

        WebSocketSession result = gameHandler.getSessionByPlayerName(playerName, gameId);

        assertEquals(session, result);
    }
}