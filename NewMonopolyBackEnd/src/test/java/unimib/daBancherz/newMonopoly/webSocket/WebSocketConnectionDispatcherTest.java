package unimib.daBancherz.newMonopoly.webSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.newMonopoly.handler.ChatHandler;
import unimib.daBancherz.newMonopoly.handler.GameHandler;
import unimib.daBancherz.newMonopoly.handler.PropertyHandler;
import unimib.daBancherz.newMonopoly.manager.TurnManager;
import unimib.daBancherz.newMonopoly.MessageService;
import unimib.daBancherz.newMonopoly.model.PlayerProperties;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WebSocketConnectionDispatcherTest {

    @Mock
    private GameHandler gameHandler;

    @Mock
    private ChatHandler chatHandler;

    @Mock
    private WebSocketReconnect reconnect;

    @Mock
    private PropertyHandler propertyHandler;

    @Mock
    private MessageService messageService;

    @Mock
    private TurnManager turnManager;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebSocketConnectionDispatcher webSocketConnectionDispatcher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webSocketConnectionDispatcher = new WebSocketConnectionDispatcher(gameHandler, chatHandler, turnManager, reconnect, propertyHandler, messageService);
    }

    @Test
    void testAfterConnectionEstablished() {
        when(session.getId()).thenReturn("sessionId");
        webSocketConnectionDispatcher.afterConnectionEstablished(session);
        assertTrue(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
    void testHandleMessage() throws Exception {
        String payload = "Ping";
        TextMessage message = new TextMessage(payload);

        when(session.getId()).thenReturn("sessionId");

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(messageService, times(1)).sendPongMessage(session);
    }

    @Test
    void testHandleMessagePing() throws Exception {
        String payload = "Ping:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(messageService, times(1)).sendPongMessage(session);
    }

    @Test
    void testHandleMessageLanciaDadi() throws Exception {
        String payload = "LanciaDadi:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).spostaPedina(session);
    }

    @Test
    void testHandleMessageFineTurno() throws Exception {
        String payload = "FineTurno:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).endTurn(session);
    }

    @Test
    void testHandleMessageInizioPartita() throws Exception {
        String payload = "InizioPartita:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).startTurn(session);
    }

    @Test
    void testHandleMessageCreate() throws Exception {
        String payload = "Create:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).createGame(any(String[].class), eq(session));
    }

    @Test
    void testHandleMessagePartecipa() throws Exception {
        String payload = "Partecipa:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).joinGame(any(String[].class), eq(session));
    }

    @Test
    void testHandleMessageMessaggioUtente() throws Exception {
        String payload = "MessaggioUtente:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(chatHandler, times(1)).chatHandler(any(String[].class), eq(session));
    }

    @Test
     void testHandleMessageSceltaPedina() throws Exception {
        String payload = "SceltaPedina:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).choosePedina(any(String[].class), eq(session));
    }

    @Test
    void testHandleMessageAcquistaProprieta() throws Exception {
        String payload = "AcquistaProprieta:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).acquistaProprieta(any(String[].class), eq(session));
    }

    @Test
     void testHandleMessagePingScambiaProprieta() throws Exception {
        String payload = "PingScambiaProprieta:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).gestisciProprieta(session);
    }

    @Test
     void testHandleMessagePagaUscitaPrigione() throws Exception {
        String payload = "PagaUscitaPrigione:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).payPrisonExit(session);
    }

    @Test
     void testHandleMessageRichiestaUpdateProperties() throws Exception {
        String payload = "RichiestaUpdateProperties:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).updateProperties(session);
    }

    @Test
     void testHandleMessageRispostaScambio() throws Exception {
        String payload = "{ \"type\": \"!RispostaScambio\", \"exchangeAccepted\": true }";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).rispostaScambio(any(Map.class), eq(true), eq(session));
    }

    @Test
     void testHandleTransportError() {
        when(session.getId()).thenReturn("sessionId");
        Throwable exception = new Exception("Test exception");

        webSocketConnectionDispatcher.handleTransportError(session, exception);

        assertFalse(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
     void testAfterConnectionClosed() throws Exception {
        when(session.getId()).thenReturn("sessionId");
        CloseStatus status = CloseStatus.NORMAL;

        webSocketConnectionDispatcher.afterConnectionClosed(session, status);

        assertFalse(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
     void testSupportsPartialMessages() {
        assertFalse(webSocketConnectionDispatcher.supportsPartialMessages());
    }

   @Test
     void testHandleMessageEffettuaScambio() throws Exception {
        String payload = "{ \"type\": \"!EffettuaScambio\", \"data\": {\"key\": \"value\"} }";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(propertyHandler, times(1)).effettuaScambio(captor.capture(), eq(session));
    }

    @Test
     void testHandleMessageCostruisciCasa() throws Exception {
        PlayerProperties property = new PlayerProperties();
        property.setNome("propId");
        String payload = "{ \"type\": \"!CostruisciCasa\", \"property\": " + new ObjectMapper().writeValueAsString(property) + ", \"casine\": 2 }";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        ArgumentCaptor<PlayerProperties> propertyCaptor = ArgumentCaptor.forClass(PlayerProperties.class);
        ArgumentCaptor<Integer> casineCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(propertyHandler, times(1)).gestisciCase(propertyCaptor.capture(), casineCaptor.capture(), eq(session));
        PlayerProperties capturedProperty = propertyCaptor.getValue();
        Integer capturedCasine = casineCaptor.getValue();
        assertEquals("propId", capturedProperty.getNome());
        assertEquals(Integer.valueOf(2), capturedCasine);
    }

    @Test
     void testHandleMessageIpotecaProprieta() throws Exception {
        PlayerProperties property = new PlayerProperties();
        property.setNome("propId");
        String payload = "{ \"type\": \"!IpotecaProprieta\", \"property\": " + new ObjectMapper().writeValueAsString(property) + "}";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        ArgumentCaptor<PlayerProperties> propertyCaptor = ArgumentCaptor.forClass(PlayerProperties.class);
        verify(propertyHandler, times(1)).ipotecaProprieta(propertyCaptor.capture(), eq(session));
    }
}
