package unimib.dabancherz.newmonopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
import unimib.dabancherz.newmonopoly.database.repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.service.GameService;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MessageServiceTest {
    @Mock
    private GameService gameService;
    @Mock
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private WebSocketSession session;
    @InjectMocks
    private MessageService messageService;
    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageService(gameService, pCPPRepository, giocatoreRepository);
    }
    @Test
     void testCreateMessage() throws IOException {
        Map<String, Object> data = Map.of("key", "value");
        String message = messageService.createMessage(data);
        assertEquals("{\"key\":\"value\"}", message);
    }
    @Test
     void testSendSystemMessage() throws Exception {
        String gameId = "game-1";
        String content = "System message";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendSystemMessage(gameId, content, gameSessions, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendUnusedPedine() throws Exception {
        String gameId = "game-1";
        List<Integer> pedineNonUsate = List.of(1, 2, 3);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendUnusedPedine(pedineNonUsate, gameSessions, gameId);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendPawnMove() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        Integer pawnId = 1;
        Integer offset = 5;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendPawnMove(pawnId, playerName, offset, gameSessions, gameId);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendPlayerPawnPosition() throws IOException {
        String playerName = "player1";
        Integer pawnId = 1;
        Integer offset = 5;
        messageService.sendPlayerPawnPosition(pawnId, playerName, offset, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendJoinMessage() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        String role = "player";
        Giocatore giocatore = new Giocatore();
        giocatore.setSaldo(100);
        giocatore.setPuntiFedelta(10);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        messageService.sendJoinMessage(playerName, gameSessions, role, gameId);
        verify(session, times(2)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendGameId() throws Exception {
        String gameId = "game-1";
        messageService.sendGameId(gameId, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendTypePlayer() throws Exception {
        String playerType = "player";
        messageService.sendTypePlayer(playerType, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendPlayerAndBalance() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        String role = "player";
        Giocatore giocatore = new Giocatore();
        giocatore.setSaldo(100);
        giocatore.setPuntiFedelta(10);
        when(gameService.getPlayersWithIdLowerThan(anyString(), anyString())).thenReturn(List.of(playerName));
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        messageService.sendPlayerAndBalance(gameId, playerName, session, role);
        verify(session, times(2)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testUpdateBalance() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        Giocatore giocatore = new Giocatore();
        giocatore.setSaldo(100);
        giocatore.setPuntiFedelta(10);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        messageService.updateBalance(gameSessions, gameId, playerName);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testRispostaGestisciProprieta() throws IOException {
        String messaggioRisposta = "message";
        messageService.rispostaGestisciProprieta(messaggioRisposta, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testRispostaAggiornaProprieta() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        List<PlayerProperties> playerPropertiesList = Collections.emptyList();
        when(pCPPRepository.findPlayerProperties(anyString(), anyString())).thenReturn(playerPropertiesList);
        messageService.rispostaAggiornaProprieta(gameId, playerName, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendErrorMessage() throws IOException {
        messageService.sendErrorMessage(session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendErrorGameIdMessage() throws IOException {
        String gameId = "game-1";
        messageService.sendErrorGameIdMessage(session, gameId);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testNotifyPlayerDisconnected() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.notifyPlayerDisconnected(gameId, playerName, gameSessions);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendDisconnected() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendDisconnected(gameSessions, gameId, playerName);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testCreateTurnMessage() throws IOException {
        String playerName = "player1";
        boolean turn = true;
        String message = messageService.createTurnMessage(turn, playerName);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedMap = objectMapper.readValue("{\"type\":\"turn\",\"content\":true,\"playername\":\"player1\"}", Map.class);
        Map<String, Object> actualMap = objectMapper.readValue(message, Map.class);
        assertEquals(expectedMap, actualMap);
    }
    @Test
    void testExitPrisonMessage() throws IOException {
        boolean flag = true;
        messageService.exitPrisonMessage(flag, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
    void testSendDiceResults() throws Exception {
        int diceR1 = 3;
        int diceR2 = 4;
        messageService.sendDiceResults(session, diceR1, diceR2);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
    void testExchangeRequestMessage() throws IOException {
        String playerName = "player1";
        PlayerProperties property1 = new PlayerProperties();
        PlayerProperties property2 = new PlayerProperties();
        int money = 100;
        messageService.exchangeRequestMessage(playerName, property1, property2, money, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testInviaMessaggio_TypeOnly() throws IOException {
        String type = "type";
        messageService.inviaMessaggio(session, type);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testInviaMessaggio_TypeAndKeyValue() throws IOException {
        String type = "type";
        String key = "key";
        String value = "value";
        messageService.inviaMessaggio(session, type, key, value);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendPongMessage() throws IOException {
        messageService.sendPongMessage(session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendVictoryMessage() throws IOException {
        messageService.sendVictoryMessage(session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendLoseMessage() throws IOException {
        messageService.sendLoseMessage(session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testUpdateProperties() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        List<PlayerProperties> giocatorePropertiesList = Collections.emptyList();
        when(pCPPRepository.findPlayerProperties(anyString(), anyString())).thenReturn(giocatorePropertiesList);
        messageService.updateProperties(gameId, playerName, session);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendChatMessage() throws Exception {
        String gameId = "game-1";
        String content = "chat message";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendChatMessage(gameId, content, gameSessions);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
     void testSendBoxOrderMessage() throws IOException {
        String gameId = "game-1";
        int[] caselle = {1, 2, 3};
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        messageService.sendBoxOrderMessage(caselle, gameSessions, gameId);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
}