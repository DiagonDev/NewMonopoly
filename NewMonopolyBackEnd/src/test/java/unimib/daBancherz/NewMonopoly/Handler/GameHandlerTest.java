package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
import unimib.daBancherz.NewMonopoly.dataBase.Service.GameService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameHandlerTest {

    @Autowired
    private GameHandler gameHandler;

    @Mock
    private MessageHandler messageHandler;

    @Mock
    private GameService gameService;

    @Mock
    private GiocatoreRepository giocatoreRepository;

    @Mock
    private PartitaRepository partitaRepository;

    @Mock
    private PedinaRepository pedinaRepository;

    private WebSocketSession session;

    @BeforeEach
    public void setUp() throws Exception {
        session = Mockito.mock(WebSocketSession.class);
    }

    @Test
    public void testCreateGame() throws Exception {
        String[] messageParts = {"Create", "player1", "easy", "random"};

        when(partitaRepository.findLastCodiceInvito()).thenReturn(null);
        when(pedinaRepository.findUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());

        gameHandler.handleGameMessage(messageParts, session);

        verify(messageHandler).sendGameId(anyString(), eq(session));
        verify(gameService).createGameAndPlayer(eq("player1"), eq("easy"), eq("random"), anyString());
        verify(messageHandler).sendSystemMessage(anyString(), anyString(), anyMap(), eq(session));
        verify(messageHandler).notifyPlayerJoin(anyString(), eq("player1"), eq(session), anyMap(), eq("ADMIN"));
        verify(messageHandler).sendTypePlayer(eq("ADMIN"), eq(session));
        verify(messageHandler).sendUnusedPedine(anyList(), anyMap(), anyString());
    }

    @Test
    public void testJoinGame() throws Exception {
        String[] messageParts = {"Partecipa", "player2", "game-1"};

        when(giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito(anyString(), anyString())).thenReturn(false);
        when(giocatoreRepository.countGiocatoriByPartita(anyString())).thenReturn(5);
        when(pedinaRepository.findUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());
        when(gameService.getUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());

        // Mock Partita
        Partita mockPartita = new Partita();
        mockPartita.setCodiceInvito("game-1");
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(mockPartita);

        // Assicurati che gameSessions contenga game-1
        List<WebSocketSession> playersInGame = new ArrayList<>();
        gameHandler.getGameSessions().put("game-1", playersInGame);

        gameHandler.handleGameMessage(messageParts, session);

        verify(gameService).addPlayer(eq("player2"), eq("game-1"));
        verify(messageHandler).notifyPlayerJoin(eq("game-1"), eq("player2"), eq(session), anyMap(), eq("giocatore"));
        verify(messageHandler).sendUnusedPedine(anyList(), anyMap(), eq("game-1"));
    }

    @Test
    public void testGetGameIdBySession() {
        String gameId = "game-1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);

        gameHandler.getGameSessions().put(gameId, playersInGame);

        String result = gameHandler.getGameIdBySession(session);

        assertEquals(gameId, result);
    }

    @Test
    public void testNotifyPlayerDisconnected() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);

        gameHandler.getGameSessions().put(gameId, playersInGame);

        gameHandler.notifyPlayerDisconnected(gameId, playerName);

        verify(session).sendMessage(any(TextMessage.class));
    }

    /*
    @Test
    public void testChoosePedina() throws Exception {
        String[] messageParts = {"ChoosePedina", "1"};
        String gameId = "game-1";
        String playerName = "player1";

        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameService.getUnusedPedineByPartita(anyString())).thenReturn(new ArrayList<>());

        gameHandler.choosePedina(messageParts, session);

        verify(giocatoreRepository).updatePedinaForGiocatore(eq(playerName), eq(1), eq(gameId));
        verify(messageHandler).sendUnusedPedine(anyList(), anyMap(), eq(gameId));
        verify(messageHandler).sendPawnMove(eq(1), eq(playerName), eq(1), anyMap(), eq(gameId));
    }


    @Test
    public void testRemovePlayerFromGame() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        List<WebSocketSession> playersInGame = new ArrayList<>();
        playersInGame.add(session);

        gameHandler.getGameSessions().put(gameId, playersInGame);
        gameHandler.playerNameList.put(playerName, session);

        when(gameService.deletePlayer(gameId, playerName)).thenReturn(true);
        when(partitaRepository.deleteByCodiceInvito(gameId)).thenReturn(true);

        gameHandler.removePlayerFromGame(gameId, session);

        verify(gameService).deletePlayer(eq(gameId), eq(playerName));
        verify(partitaRepository).deleteByCodiceInvito(eq(gameId));
        assertFalse(gameHandler.getGameSessions().containsKey(gameId));
        assertFalse(gameHandler.playerNameList.containsKey(playerName));
    }

    @Test
    public void testGetSessionByPlayerName() {
        String playerName = "player1";
        String gameId = "game-1";
        session.getAttributes().put("gameId", gameId);
        gameHandler.playerNameList.put(playerName, session);

        WebSocketSession result = gameHandler.getSessionByPlayerName(playerName, gameId);

        assertEquals(session, result);
    }

    @Test
    public void testGetPlayerNameBySession() {
        String playerName = "player1";
        gameHandler.playerNameList.put(playerName, session);

        String result = gameHandler.getPlayerNameBySession(session);

        assertEquals(playerName, result);
    } */
}