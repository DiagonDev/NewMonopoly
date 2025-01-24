/*package unimib.daBancherz.NewMonopoly.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.ChatHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.PropertyHandler;
import unimib.daBancherz.NewMonopoly.Manager.TurnManager;
import unimib.daBancherz.NewMonopoly.MessageService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebSocketConnectionDispatcherTest {

    @Mock
    private GameHandler gameHandler;

    @Mock
    private ChatHandler chatHandler;

    @Mock
    private TurnManager turnManager;

    @Mock
    private WebSocketReconnect reconnect;

    @Mock
    private PropertyHandler propertyHandler;

    @Mock
    private MessageService messageService;

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
    public void testAfterConnectionEstablished() {
        when(session.getId()).thenReturn("sessionId");
        webSocketConnectionDispatcher.afterConnectionEstablished(session);
        assertTrue(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
    public void testHandleMessage() throws Exception {
        String payload = "Ping";
        TextMessage message = new TextMessage(payload);

        when(session.getId()).thenReturn("sessionId");

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(messageService, times(1)).sendPongMessage(session);
    }

    @Test
    public void testHandleMessagePing() throws Exception {
        String payload = "Ping:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(messageService, times(1)).sendPongMessage(session);
    }

    @Test
    public void testHandleMessageLanciaDadi() throws Exception {
        String payload = "LanciaDadi:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).spostaPedina(session);
    }

    @Test
    public void testHandleMessageFineTurno() throws Exception {
        String payload = "FineTurno:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).endTurn(session);
    }

    @Test
    public void testHandleMessageInizioPartita() throws Exception {
        String payload = "InizioPartita:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).startTurn(session);
    }

    @Test
    public void testHandleMessageCreate() throws Exception {
        String payload = "Create:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).createGame(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessagePartecipa() throws Exception {
        String payload = "Partecipa:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).joinGame(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageMessaggioUtente() throws Exception {
        String payload = "MessaggioUtente:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(chatHandler, times(1)).chatHandler(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageSceltaPedina() throws Exception {
        String payload = "SceltaPedina:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(gameHandler, times(1)).choosePedina(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageAcquistaProprieta() throws Exception {
        String payload = "AcquistaProprieta:someData";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).acquistaProprieta(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessagePingScambiaProprieta() throws Exception {
        String payload = "PingScambiaProprieta:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).gestisciProprieta(session);
    }

    @Test
    public void testHandleMessagePagaUscitaPrigione() throws Exception {
        String payload = "PagaUscitaPrigione:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(turnManager, times(1)).payPrisonExit(session);
    }

    @Test
    public void testHandleMessageRichiestaUpdateProperties() throws Exception {
        String payload = "RichiestaUpdateProperties:";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).updateProperties(session);
    }

    @Test
    public void testHandleMessageRispostaScambio() throws Exception {
        String payload = "{ \"type\": \"!RispostaScambio\", \"exchangeAccepted\": true }";
        TextMessage message = new TextMessage(payload);

        webSocketConnectionDispatcher.handleMessage(session, message);

        verify(propertyHandler, times(1)).rispostaScambio(any(Map.class), eq(true), eq(session));
    }

    @Test
    public void testHandleTransportError() {
        when(session.getId()).thenReturn("sessionId");
        Throwable exception = new Exception("Test exception");

        webSocketConnectionDispatcher.handleTransportError(session, exception);

        assertFalse(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
    public void testAfterConnectionClosed() throws Exception {
        when(session.getId()).thenReturn("sessionId");
        CloseStatus status = CloseStatus.NORMAL;

        webSocketConnectionDispatcher.afterConnectionClosed(session, status);

        assertFalse(webSocketConnectionDispatcher.playerSessions.containsKey("sessionId"));
    }

    @Test
    public void testSupportsPartialMessages() {
        assertFalse(webSocketConnectionDispatcher.supportsPartialMessages());
    }
}*/
