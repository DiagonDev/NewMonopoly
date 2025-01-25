package unimib.dabancherz.newmonopoly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.dabancherz.newmonopoly.database.service.GameService;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private MessageService messageService;
    private GameService gameService;
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private GiocatoreRepository giocatoreRepository;
    private WebSocketSession session;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        pCPPRepository = mock(PartitaCasellaPrezzoproprietaRepository.class);
        giocatoreRepository = mock(GiocatoreRepository.class);
        session = mock(WebSocketSession.class);
        objectMapper = new ObjectMapper();
        messageService = new MessageService(gameService, pCPPRepository, giocatoreRepository);
    }

    @Test
    void testCreateMessage() throws IOException {
        Map<String, Object> data = Map.of("key", "value");
        String json = messageService.createMessage(data);
        assertEquals(objectMapper.writeValueAsString(data), json);
    }

    @Test
    void testSendSystemMessage() throws Exception {
        String gameId = "game1";
        String content = "System message";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        messageService.sendSystemMessage(gameId, content, gameSessions, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "system", "content", content));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendUnusedPedine() throws Exception {
        String gameId = "game1";
        List<Integer> pedineNonUsate = List.of(1, 2, 3);
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        messageService.sendUnusedPedine(pedineNonUsate, gameSessions, gameId);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "pawnsAvailable", "content", pedineNonUsate));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendPawnMove() throws IOException {
        String gameId = "game1";
        Integer pawnId = 1;
        String playerName = "player1";
        Integer offset = 3;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        messageService.sendPawnMove(pawnId, playerName, offset, gameSessions, gameId);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "pawnMove", "pawnId", pawnId, "playerName", playerName, "offset", offset));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendJoinMessage() throws IOException {
        String playerName = "player1";
        String role = "player";
        String gameId = "game1";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(1000);

        messageService.sendJoinMessage(playerName, gameSessions, role, gameId);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(2)).sendMessage(messageCaptor.capture());
        List<TextMessage> capturedMessages = messageCaptor.getAllValues();

        String expectedJoinMessage = objectMapper.writeValueAsString(Map.of("type", "join", "playerName", playerName, "userRole", role));
        String expectedPlayersListMessage = objectMapper.writeValueAsString(Map.of("type", "playersList", "playerName", playerName, "balance", 1000));

        assertEquals(expectedJoinMessage, capturedMessages.get(0).getPayload());
        assertEquals(expectedPlayersListMessage, capturedMessages.get(1).getPayload());
    }

    @Test
    void testSendGameId() throws Exception {
        String gameId = "game1";

        messageService.sendGameId(gameId, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "gameId", "content", gameId));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendTypePlayer() throws Exception {
        String playerType = "player";

        messageService.sendTypePlayer(playerType, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "user", "content", playerType));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testNotifyPlayerJoin() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        String role = "player";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(1000);
        when(giocatoreRepository.saldoGiocatore("player2", gameId)).thenReturn(1000); // Mock saldo per player2
        when(gameService.getPlayersWithIdLowerThan(gameId, playerName)).thenReturn(List.of("player2"));

        messageService.notifyPlayerJoin(gameId, playerName, session, gameSessions, role);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(6)).sendMessage(messageCaptor.capture());
        List<TextMessage> capturedMessages = messageCaptor.getAllValues();

        String expectedSystemMessage1 = new ObjectMapper().writeValueAsString(Map.of("type", "system", "content", "Ti sei unito alla partita con ID: " + gameId + " con successo!"));
        String expectedSystemMessage2 = new ObjectMapper().writeValueAsString(Map.of("type", "system", "content", playerName + " si è unito alla partita!"));
        String expectedPlayersListMessage1 = new ObjectMapper().writeValueAsString(Map.of("type", "playersList", "playerName", "player2", "balance", 1000));
        String expectedJoinMessage1 = new ObjectMapper().writeValueAsString(Map.of("type", "join", "playerName", playerName, "userRole", role));
        String expectedJoinMessage2 = new ObjectMapper().writeValueAsString(Map.of("type", "join", "playerName", playerName, "userRole", role));

        assertEquals(expectedSystemMessage1, capturedMessages.get(0).getPayload());
        assertEquals(expectedSystemMessage2, capturedMessages.get(1).getPayload());
        assertEquals(expectedPlayersListMessage1, capturedMessages.get(2).getPayload());
        assertEquals(expectedJoinMessage1, capturedMessages.get(3).getPayload());
        assertEquals(expectedJoinMessage2, capturedMessages.get(4).getPayload());
    }

    @Test
    void testUpdateBalance() throws IOException {
        String gameId = "game1";
        String playerName = "player1";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(giocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(1000);

        messageService.updateBalance(gameSessions, gameId, playerName);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "playerBalance", "playerName", playerName, "balance", 1000));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testRispostaGestisciProprieta() throws IOException {
        String messaggioRisposta = "Gestisci proprietà";
        messageService.rispostaGestisciProprieta(messaggioRisposta, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "rispostaGestisciProprieta", "content", messaggioRisposta));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testRispostaAggiornaProprieta() throws IOException {
        String gameId = "game1";
        String playerName = "player1";
        List<PlayerProperties> playerPropertiesList = List.of(new PlayerProperties());
        when(pCPPRepository.findPlayerProperties(gameId, playerName)).thenReturn(playerPropertiesList);

        messageService.rispostaAggiornaProprieta(gameId, playerName, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "rispostaAggiornaProprieta", "properties", playerPropertiesList));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendErrorMessage() throws IOException {
        messageService.sendErrorMessage(session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "errorName"));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendErrorGameIdMessage() throws IOException {
        String gameId = "game1";

        messageService.sendErrorGameIdMessage(session, gameId);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "errorGameId", "gameId", gameId));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testNotifyPlayerDisconnected() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        messageService.notifyPlayerDisconnected(gameId, playerName, gameSessions);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "system", "content", playerName + " si è disconnesso dalla partita."));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testCreateTurnMessage() throws IOException {
        boolean turn = true;
        String playerName = "player1";

        String turnMessage = messageService.createTurnMessage(turn, playerName);

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "turn", "content", turn, "playername", playerName));
        assertEquals(expectedMessage, turnMessage);
    }

    @Test
    void testExitPrisonMessage() throws IOException {
        boolean flag = true;

        messageService.exitPrisonMessage(flag, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "exitPrison", "flag", flag));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendDiceResults() throws Exception {
        int diceR1 = 4;
        int diceR2 = 5;

        messageService.sendDiceResults(session, diceR1, diceR2);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "diceRolled", "dice1", diceR1, "dice2", diceR2));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testExchangeRequestMessage() throws IOException {
        String nomeRichiedente = "player1";
        PlayerProperties property1 = new PlayerProperties();
        PlayerProperties property2 = new PlayerProperties();
        Integer money = 1000;

        messageService.exchangeRequestMessage(nomeRichiedente, property1, property2, money, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "exchangeRequest", "property1", property1, "property2", property2, "money", money, "playerName", nomeRichiedente));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testInviaMessaggio() throws IOException {
        String type = "type";
        messageService.inviaMessaggio(session, type);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", type));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testInviaMessaggioWithKeyValue() throws IOException {
        String type = "type";
        String key = "key";
        Object value = "value";

        messageService.inviaMessaggio(session, type, key, value);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", type, key, value));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendPongMessage() throws IOException {
        messageService.sendPongMessage(session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "pong", "content", "pong"));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendVictoryMessage() throws IOException {
        messageService.sendVictoryMessage(session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "partitaFinita", "flag", "vittoria", "content", "Hai vinto"));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendLoseMessage() throws IOException {
        messageService.sendLoseMessage(session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "partitaFinita", "flag", "sconfitta", "content", "Il tuo saldo è negativo"));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testUpdateProperties() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        List<PlayerProperties> giocatorePropertiesList = List.of(new PlayerProperties());
        when(pCPPRepository.findPlayerProperties(gameId, playerName)).thenReturn(giocatorePropertiesList);

        messageService.updateProperties(gameId, playerName, session);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "updateProperties", "properties", giocatorePropertiesList));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }

    @Test
    void testSendChatMessage() throws Exception {
        String gameId = "game1";
        String content = "Chat message";
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));

        messageService.sendChatMessage(gameId, content, gameSessions);

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());
        TextMessage capturedMessage = messageCaptor.getValue();

        String expectedMessage = objectMapper.writeValueAsString(Map.of("type", "chat", "content", content));
        assertEquals(expectedMessage, capturedMessage.getPayload());
    }
}